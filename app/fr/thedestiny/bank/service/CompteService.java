package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.OperationDao;
import fr.thedestiny.bank.dao.SoldeDao;
import fr.thedestiny.bank.dao.TypeCompteDao;
import fr.thedestiny.bank.dto.CompteDto;
import fr.thedestiny.bank.dto.StatsDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class CompteService extends AbstractService {

	@Autowired
	private CompteDao compteDao;

	@Autowired
	private TypeCompteDao typeCompteDao;

	@Autowired
	private SoldeDao soldeDao;

	@Autowired
	private OperationDao operationDao;

	private CompteService() {
		super("bank");
	}

	public List<CompteDto> listCompteForUser(final int userId) {

		// TODO CompteDTO has weird struct.
		List<CompteDto> result = new ArrayList<>();
		for (Compte current : compteDao.findAll(null, userId)) {
			result.add(new CompteDto(current));
		}

		return result;
	}

	public CompteDto findById(final int userId, final int currentUser) {
		Compte fetched = compteDao.findById(null, userId);
		if (!fetched.getOwner().equals(currentUser)) {
			throw new SecurityException();
		}

		return new CompteDto(fetched);
	}

	public CompteDto saveCompte(final Compte compte, final int currentUser) {

		if (compte.getId() != null) {
			throw new IllegalArgumentException("Cannot have ID.");
		}

		return this.processInTransaction(new InTransactionFunction<CompteDto>() {

			@Override
			public CompteDto doWork(EntityManager em) {

				compte.setOwner(currentUser);
				compte.setLastUpdate(new Date());
				compte.setSolde(.0d);

				TypeCompte type = typeCompteDao.findById(em, compte.getType().getId());
				if (type == null) {
					throw new SecurityException();
				}

				return new CompteDto(compteDao.save(em, compte));
			}
		});
	}

	public CompteDto updateAccount(final Compte compte, final int currentUser) {

		if (compte.getId() == null) {
			throw new IllegalArgumentException("ID mandatory.");
		}

		return this.processInTransaction(new InTransactionFunction<CompteDto>() {

			@Override
			public CompteDto doWork(EntityManager em) {

				Compte persistedOne = compteDao.findById(em, compte.getId());
				if (!persistedOne.getOwner().equals(currentUser)) {
					throw new SecurityException();
				}

				TypeCompte type = typeCompteDao.findById(em, compte.getType().getId());
				if (type == null) {
					throw new SecurityException();
				}

				persistedOne.setLastUpdate(new Date());

				persistedOne.setNom(compte.getNom());
				persistedOne.setType(type);
				persistedOne.setActive(compte.isActive());

				return new CompteDto(persistedOne);
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

	protected boolean isAccountOwnedByUser(final int accountId, final int userId) {
		Compte compte = compteDao.findById(null, accountId);
		return compte.getOwner().equals(userId);
	}

	public StatsDto getAccountStatsForLastYear(final int userId, final int accountId) {

		Compte compte = compteDao.findById(null, accountId);
		if (compte.getOwner().equals(userId) == false) {
			throw new SecurityException();
		}

		StatsDto dto = new StatsDto();
		dto.setAccountId(accountId);

		MoisAnnee lastYearMonth = null;
		Solde lastSolde = null;

		YearMonth minValue = YearMonth.now().minusYears(1);
		for (MoisAnnee current : compte.getMois()) {

			// Only retain the 12 past months by excluding others.
			// Note: JodaTime use 1-indexed months
			YearMonth yearMonth = new YearMonth(current.getAnnee(), current.getMois() + 1);
			if (yearMonth.isBefore(minValue)) {
				continue;
			}

			Solde solde = soldeDao.find(null, accountId, current.getId());
			if (solde != null) {
				dto.addStat(current.getMois(), solde.getSolde());
				lastSolde = solde;
			} else {
				dto.addStat(current.getMois(), 0);
			}

			lastYearMonth = current;
		}

		// Last month can have operations. Need to add amounts to get final balance.
		List<Operation> operations = operationDao.findAll(null, accountId, lastYearMonth.getId());
		double balance = lastSolde.getSolde();
		for (Operation current : operations) {
			balance += current.getMontant();
		}

		dto.addStat((lastYearMonth.getMois() + 1) % 12, balance);

		return dto;
	}
}
