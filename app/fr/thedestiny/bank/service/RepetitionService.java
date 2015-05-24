package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.Constants;
import fr.thedestiny.bank.dao.RepetitionDao;
import fr.thedestiny.bank.dto.RepetitionDto;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.models.Repetition;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;
import fr.thedestiny.global.service.InTransactionProcedure;

@Service
public class RepetitionService extends AbstractService {

	@Autowired
	private RepetitionDao repetitionDao;

	@Autowired
	private CompteService compteService;

	private RepetitionService() {
		super(Constants.BANK_CONTEXT);
	}

	public void save(final Operation op) {

		final Repetition repetition = new Repetition();
		repetition.setCompte(op.getCompte());
		repetition.setNom(op.getNom());
		repetition.setActive(true);
		repetition.setType(op.getType());
		repetition.setMontant(op.getMontant());

		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) throws Exception {
				repetitionDao.persist(em, repetition);
			}
		});
	}

	public List<RepetitionDto> findByAccount(final Integer accountId, final Integer userId) {

		if (!compteService.isAccountOwnedByUser(accountId, userId)) {
			throw new SecurityException();
		}

		List<RepetitionDto> dto = new ArrayList<>();
		for (Repetition current : repetitionDao.findByAccount(accountId)) {
			dto.add(new RepetitionDto(current));
		}

		return dto;
	}

	public boolean deleteRepetition(final Integer accountId, final Integer id, final Integer userId) {

		if (!compteService.isAccountOwnedByUser(accountId, userId)) {
			throw new SecurityException();
		}

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws Exception {
				return repetitionDao.delete(em, id, accountId);
			}
		});
	}
}
