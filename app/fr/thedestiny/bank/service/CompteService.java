package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;

import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.SoldeDao;
import fr.thedestiny.bank.dto.CompteDto;
import fr.thedestiny.bank.dto.StatsDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;

public class CompteService extends AbstractService {

	private static CompteService thisInstance = new CompteService();

	private CompteDao compteDao;
	private SoldeDao soldeDao;

	public static CompteService getInstance() {
		return thisInstance;
	}

	private CompteService() {
		super("bank");
		compteDao = new CompteDao("bank");
		soldeDao = new SoldeDao("bank");
	}

	public List<CompteDto> listCompteForUser(Integer userId) {

		List<CompteDto> result = new ArrayList<CompteDto>();
		for (Compte current : compteDao.findAll(null, userId)) {
			CompteDto dto = new CompteDto(current);
			result.add(dto);
		}

		return result;
	}

	public CompteDto saveCompte(final GenericModelDto<Compte> dto, final Integer currentUser) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public CompteDto doWork(EntityManager em) throws Exception {
				Compte c = dto.asObject();

				if (c == null) {
					throw new MappingException("Cannot map dto with model.");
				}

				// If editing a new account, check owner
				if (c.getId() != null) {
					Compte persistedOne = compteDao.findById(em, c.getId());
					if (!persistedOne.getOwner().equals(currentUser)) {
						throw new SecurityException();
					}
				}

				c.setOwner(currentUser);
				c.setLastUpdate(new Date());

				c = compteDao.save(em, c);
				return new CompteDto(c);
			}
		});
	}

	public void deleteCompte(final Integer id) throws Exception {

		this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public Object doWork(EntityManager em) throws Exception {
				compteDao.delete(em, id);
				return null;
			}
		});
	}

	public StatsDto getStatsPerMonthForAccount(Integer userId, Integer accountId) throws Exception {

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
