package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.SoldeDao;
import fr.thedestiny.bank.dto.CompteDto;
import fr.thedestiny.bank.dto.StatsDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class CompteService extends AbstractService {

	@Autowired
	private CompteDao compteDao;

	@Autowired
	private SoldeDao soldeDao;

	private CompteService() {
		super("bank");
	}

	public List<CompteDto> listCompteForUser(final int userId) {

		List<CompteDto> result = new ArrayList<>();
		for (Compte current : compteDao.findAll(null, userId)) {
			result.add(new CompteDto(current));
		}

		return result;
	}

	public CompteDto saveCompte(final Compte compte, final int currentUser) {

		return this.processInTransaction(new InTransactionFunction<CompteDto>() {

			@Override
			public CompteDto doWork(EntityManager em) {

				// If editing a new account, check owner
				if (compte.getId() != null) {
					Compte persistedOne = compteDao.findById(em, compte.getId());
					if (!persistedOne.getOwner().equals(currentUser)) {
						throw new SecurityException();
					}
				}

				compte.setOwner(currentUser);
				compte.setLastUpdate(new Date());

				return new CompteDto(compteDao.save(em, compte));
			}
		});
	}

	public boolean deleteCompte(final int id) {

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws Exception {
				return compteDao.delete(em, id);
			}
		});
	}

	public StatsDto getStatsPerMonthForAccount(final int userId, final int accountId) {

		Compte compte = compteDao.findById(null, accountId);
		if (compte.getOwner().equals(userId) == false) {
			throw new SecurityException();
		}

		StatsDto dto = new StatsDto();
		dto.setAccountId(accountId);

		for (MoisAnnee current : compte.getMois()) {

			Solde solde = soldeDao.find(null, accountId, current.getId());
			if (solde != null) {
				dto.addStat(current.getId(), solde.getSolde());
			}
		}

		return dto;
	}
}
