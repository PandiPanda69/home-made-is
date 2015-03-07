package fr.thedestiny.bank.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.OperationTypeDao;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class OperationTypeService extends AbstractService {

	@Autowired
	private OperationTypeDao operationTypeDao;

	private OperationTypeService() {
		super("bank");
	}

	public List<OperationType> findAllOperationTypes() {
		return operationTypeDao.findAll(null);
	}

	public OperationType addOperationType(final OperationType op) {

		assert op.getId() == null;

		return this.processInTransaction(new InTransactionFunction<OperationType>() {

			@Override
			public OperationType doWork(EntityManager em) {
				return operationTypeDao.save(em, op);
			}
		});
	}

	public OperationType updateOperationType(final OperationType op) {

		assert op.getId() != null;

		return this.processInTransaction(new InTransactionFunction<OperationType>() {

			@Override
			public OperationType doWork(EntityManager em) {
				return operationTypeDao.save(em, op);
			}
		});
	}

	public boolean deleteOperationType(final int id) {

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) {
				return operationTypeDao.delete(em, id);
			}
		});
	}
}
