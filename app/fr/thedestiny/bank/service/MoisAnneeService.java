package fr.thedestiny.bank.service;

import java.security.InvalidParameterException;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;

import play.Logger;
import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.MoisAnneeDao;
import fr.thedestiny.bank.dto.GenericModelDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;

public class MoisAnneeService extends AbstractService {

	private static MoisAnneeService thisInstance = new MoisAnneeService();

	private CompteDao compteDao;
	private MoisAnneeDao monthDao;

	public static MoisAnneeService getInstance() {
		return thisInstance;
	}

	private MoisAnneeService() {
		super("bank");
		compteDao = new CompteDao("bank");
		monthDao = new MoisAnneeDao("bank");
	}

	public List<MoisAnnee> findAccountMonths(Integer userId, Integer compteId) throws SecurityException {

		// Check user owns account
		Compte compte = compteDao.findById(null, compteId);
		if (compte.getOwner().equals(userId) == false) {
			throw new SecurityException("Le compte n'appartient pas à l'utilisateur.");
		}

		return compte.getMois();
	}

	public GenericModelDto<MoisAnnee> addMonth(final GenericModelDto<MoisAnnee> dto, final Integer accountId, final Integer userId) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public GenericModelDto<MoisAnnee> doWork(EntityManager em) throws Exception {

				MoisAnnee mois = dto.asObject();

				if (mois == null) {
					throw new MappingException("Cannot map dto with model.");
				}

				// Vérification que le mois existe déjà ou non en base pour le sauvegarder si nécessaire.
				MoisAnnee persisted = monthDao.findUnique(em, mois.getMois(), mois.getAnnee());
				if (persisted == null) {
					mois = monthDao.save(em, mois);
				} else {
					mois = persisted;
				}

				// Récupération du compte pour affecter le mois en vérifiant que le compte appartient bien à l'utilisateur courant
				Compte compte = compteDao.findById(em, accountId);

				if (!compte.getOwner().equals(userId)) {
					Logger.error("User " + userId + " tried to access to user " + compte.getOwner() + " account.");
					throw new SecurityException();
				}

				// Vérifie que le compte ne possède pas déjà le mois
				if (compte.isMonthLinked(mois)) {
					Logger.error("Account (" + accountId + ") is already linked to this month (" + mois.getId() + ").");
					throw new InvalidParameterException();
				}

				compte.getMois().add(mois);
				compteDao.save(em, compte);

				return new GenericModelDto<MoisAnnee>(mois);
			}
		});
	}
}
