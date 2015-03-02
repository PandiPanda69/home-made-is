package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;

import fr.thedestiny.bank.dao.MotifOperationDao;
import fr.thedestiny.bank.dto.MotifOperationDto;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;
import fr.thedestiny.global.service.InTransactionProcedure;

public class MotifOperationService extends AbstractService {

	private static MotifOperationService thisInstance = new MotifOperationService();

	private MotifOperationDao motifDao;

	protected MotifOperationService() {
		super("bank");
		this.motifDao = new MotifOperationDao("bank");
	}

	public static MotifOperationService getInstance() {
		return thisInstance;
	}

	public List<MotifOperationDto> findAllMotifs(Integer userId) {

		List<MotifOperationDto> result = new ArrayList<MotifOperationDto>();
		for (MotifOperation motif : motifDao.findAll(userId)) {
			result.add(new MotifOperationDto(motif));
		}

		return result;
	}

	public void deleteMotif(final Integer userId, final Integer motifId) throws Exception {

		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) throws Exception {
				motifDao.delete(em, userId, motifId);
			}
		});
	}

	public MotifOperationDto addMotif(final GenericModelDto<MotifOperation> dto, final Integer userId) throws Exception {

		return this.processInTransaction(new InTransactionFunction() {

			@SuppressWarnings("unchecked")
			@Override
			public MotifOperationDto doWork(EntityManager em) throws Exception {

				MotifOperation motif = dto.asObject();
				if (motif == null) {
					throw new MappingException("Cannot map dto with model.");
				}

				if (motif.getId() != null) {
					throw new Exception("Cannot have Id.");
				}

				// Vérification que la regex est valide (sinon lève une exception)
				"".matches(motif.getMotif());

				// TODO Checker que le même motif n'existe pas déjà pour le user?

				// Tout est ok donc on peut setter le user et persister.
				motif.setUserId(userId);
				motifDao.save(em, motif);

				return new MotifOperationDto(motif);
			}
		});
	}
}
