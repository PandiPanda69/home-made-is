package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.MotifOperationDao;
import fr.thedestiny.bank.dto.MotifOperationDto;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class MotifOperationService extends AbstractService {

	@Autowired
	private MotifOperationDao motifDao;

	protected MotifOperationService() {
		super("bank");
	}

	public List<MotifOperationDto> findAllMotifs(final int userId) {

		List<MotifOperationDto> result = new ArrayList<>();
		for (MotifOperation motif : motifDao.findAll(userId)) {
			result.add(new MotifOperationDto(motif));
		}

		return result;
	}

	public boolean deleteMotif(final int userId, final int motifId) {

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws Exception {
				return motifDao.delete(em, userId, motifId);
			}
		});
	}

	public MotifOperationDto addMotif(final MotifOperation motif, final int userId) {

		assert motif.getId() == null;

		return this.processInTransaction(new InTransactionFunction<MotifOperationDto>() {

			@Override
			public MotifOperationDto doWork(EntityManager em) {

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
