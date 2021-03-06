package fr.thedestiny.bank.service;

import java.security.InvalidParameterException;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.MoisAnneeDao;
import fr.thedestiny.bank.dao.OperationDao;
import fr.thedestiny.bank.dao.SoldeDao;
import fr.thedestiny.bank.dto.SoldeDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;
import fr.thedestiny.global.service.InTransactionProcedure;

@Service
public class SoldeService extends AbstractService {

	@Autowired
	private CompteDao compteDao;

	@Autowired
	private MoisAnneeDao monthDao;

	@Autowired
	private SoldeDao soldeDao;

	@Autowired
	private OperationDao operationDao;

	private SoldeService() {
		super("bank");
	}

	public SoldeDto getBalanceAtBeginningOfMonth(final int userId, final int idCompte, final int idMois) {

		return this.processInTransaction(new InTransactionFunction<SoldeDto>() {

			@Override
			public SoldeDto doWork(EntityManager em) {

				// Vérification que le compte appartient à l'utilisateur
				Compte compte = compteDao.findById(em, idCompte);
				if (compte.getOwner().equals(userId) == false) {
					throw new SecurityException();
				}

				// Vérification que le compte possède le mois
				MoisAnnee mois = monthDao.findById(em, idMois);
				if (compte.isMonthLinked(mois) == false) {
					throw new InvalidParameterException();
				}

				Solde solde = getSoldeForMonth(em, compte, mois);
				return new SoldeDto(solde);
			}
		});
	}

	/**
	 * Récupération du solde pour le mois demandé
	 * @param compte
	 * @param mois
	 * @return
	 */
	private Solde getSoldeForMonth(EntityManager em, final Compte compte, final MoisAnnee mois) {
		Solde solde = null;
		boolean saveSolde = false;

		// Récupération des soldes précalculés pour le compte
		List<Solde> soldes = soldeDao.findAll(null, compte.getId());

		// Pas de soldes enregistrés, donc on détermine depuis le début...
		if (soldes == null || soldes.isEmpty()) {
			Double balance = computeBalanceFromBeginning(em, compte, mois);

			// Création du solde
			solde = new Solde(compte, mois, balance);
			saveSolde = true;
		}
		// Sinon, on calcule du dernier solde jusqu'au début du mois courant
		else {
			Solde last = getSoldeForMonth(soldes, mois, false);

			// Aucune correspondance trouvée...
			if (last == null) {
				Double balance = computeBalanceFromBeginning(em, compte, mois);

				// Création du solde
				solde = new Solde(compte, mois, balance);
				saveSolde = true;
			}
			// Le mois correspond, on a notre résultat !
			else if (last.getMois().equals(mois)) {
				solde = last;
			}
			// On calcule du dernier trouvé jusqu'au mois voulu
			else {
				Double balance = computeBalanceForMonth(em, compte, last, mois);

				// Création du solde
				solde = new Solde(compte, mois, balance);
				saveSolde = true;
			}
		}

		if (saveSolde) {
			solde = soldeDao.persistSolde(em, solde);
		}

		return solde;
	}

	public void updateSolde(final int userId, final int accountId, final int monthId) {

		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) {

				Compte compte = compteDao.findById(em, accountId);
				if (compte == null || compte.getOwner().equals(userId) == false) {
					throw new SecurityException();
				}

				MoisAnnee month = monthDao.findById(em, monthId);

				Solde solde = getSoldeForMonth(em, compte, month);
				updateSolde(em, solde);
			}
		});
	}

	/**
	 * Met à jour le solde en question en calculant également ses suivants
	 * @param solde
	 */
	private void updateSolde(EntityManager em, final Solde solde) {

		Double balance = null;
		List<Solde> soldes = soldeDao.findAll(em, solde.getCompte().getId());

		// On récupère le solde précédent
		Solde previous = getSoldeForMonth(soldes, solde.getMois(), true);

		// ... mais s'il y en a pas on recalcule depuis le début
		if (previous == null) {
			balance = computeBalanceFromBeginning(em, solde.getCompte(), solde.getMois());
		}
		// On détermine le nouveau solde
		else {
			balance = computeBalanceForMonth(em, solde.getCompte(), previous, solde.getMois());
		}

		// MaJ du solde
		solde.setSolde(balance);
		soldeDao.save(em, solde);

		// Et récursivement, on passe au suivant...
		MoisAnnee nextMois = solde.getCompte().getNextMonth(solde.getMois());
		if (nextMois != null) {
			Solde nextSolde = soldeDao.find(em, solde.getCompte().getId(), nextMois.getId());
			if (nextSolde != null) {
				updateSolde(em, nextSolde);
			}
		}
	}

	/**
	 * Détermine le solde depuis la toute première opération enregistrée.
	 * @param compte
	 * @param mois
	 * @return
	 */
	protected Double computeBalanceFromBeginning(EntityManager em, final Compte compte, final MoisAnnee mois) {

		Double balance = .0d;
		for (MoisAnnee current : compte.getMois()) {
			// Stop dès que l'on arrive au mois voulu
			if (current.equals(mois)) {
				break;
			}

			balance += sumOperationsAmount(em, compte, current);
		}

		return balance;
	}

	/**
	 * Détermine le solde à partir du solde passé en paramètre jusqu'au mois cible
	 * @param compte
	 * @param solde
	 * @param cible
	 * @return
	 */
	protected Double computeBalanceForMonth(EntityManager em, final Compte compte, final Solde solde, final MoisAnnee cible) {

		Double balance = solde.getSolde();

		// On ajoute les montants pour les mois compris entre le solde obtenu et le mois cible.
		MoisAnnee current = null;
		for (int i = 0; i < compte.getMois().size(); i++) {

			current = compte.getMois().get(i);

			// Cible < Courant < Solde
			if ((solde.getMois().lesserThan(current) || solde.getMois().equals(current)) && current.lesserThan(cible)) {
				balance += sumOperationsAmount(em, compte, current);
			}
		}

		return balance;
	}

	/**
	 * Somme l'ensemble des opérations d'un mois donné
	 * @param compte
	 * @param mois
	 * @return La somme des montants
	 */
	protected Double sumOperationsAmount(EntityManager em, final Compte compte, final MoisAnnee mois) {
		Double balance = .0d;
		List<Operation> operations = operationDao.findAll(em, compte.getId(), mois.getId());
		for (Operation op : operations) {
			balance += op.getMontant();
		}

		return balance;
	}

	/**
	 * Récupère le solde du mois demandé, ou à défaut, du plus proche précédent
	 * @param soldes
	 * @param mois
	 * @param getPrevious	Force à récupérer le solde précédent
	 * @return
	 */
	private Solde getSoldeForMonth(final List<Solde> soldes, final MoisAnnee mois, final boolean getPrevious) {

		int i = 0;
		Solde result = null;
		Solde current = null;

		do {
			current = soldes.get(i);

			// Result (non null) < Courant < Cible
			if ((result == null || result.getMois().lesserThan(current.getMois())) && current.getMois().lesserThan(mois)) {
				result = current;
			}

			// Bingo ! (mais que si on accepte de le récupérer)
			if (!getPrevious && current.getMois().equals(mois)) {
				result = current;
				break;
			}

			i++;
		} while (i < soldes.size());

		return result;
	}
}
