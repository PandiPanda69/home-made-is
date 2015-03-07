package fr.thedestiny.bank.service;

import java.security.InvalidParameterException;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.MoisAnneeDao;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class MoisAnneeService extends AbstractService {

	@Autowired
	private CompteDao compteDao;

	@Autowired
	private MoisAnneeDao monthDao;

	private MoisAnneeService() {
		super("bank");
	}

	public List<MoisAnnee> findAccountMonths(final int userId, final int compteId) throws SecurityException {

		// Check user owns account
		Compte compte = compteDao.findById(null, compteId);
		if (compte.getOwner().equals(userId) == false) {
			throw new SecurityException("Le compte n'appartient pas à l'utilisateur.");
		}

		return compte.getMois();
	}

	public MoisAnnee addMonth(final MoisAnnee mois, final int accountId, final int userId) {

		return this.processInTransaction(new InTransactionFunction<MoisAnnee>() {

			@Override
			public MoisAnnee doWork(EntityManager em) {

				// Vérification que le mois existe déjà ou non en base pour le sauvegarder si nécessaire.
				MoisAnnee persisted = monthDao.findUnique(em, mois.getMois(), mois.getAnnee());
				if (persisted == null) {
					persisted = monthDao.save(em, mois);
				}

				// Récupération du compte pour affecter le mois en vérifiant que le compte appartient bien à l'utilisateur courant
				Compte compte = compteDao.findById(em, accountId);

				if (!compte.getOwner().equals(userId)) {
					Logger.error("User " + userId + " tried to access to user " + compte.getOwner() + " account.");
					throw new SecurityException();
				}

				// Vérifie que le compte ne possède pas déjà le mois
				if (compte.isMonthLinked(persisted)) {
					Logger.error("Account (" + accountId + ") is already linked to this month (" + persisted.getId() + ").");
					throw new InvalidParameterException();
				}

				compte.getMois().add(persisted);
				compteDao.save(em, compte);

				return persisted;
			}
		});
	}
}
