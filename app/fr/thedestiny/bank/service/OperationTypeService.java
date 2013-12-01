package fr.thedestiny.bank.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;

import fr.thedestiny.bank.dao.OperationTypeDao;
import fr.thedestiny.bank.dto.GenericModelDto;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;

public class OperationTypeService extends AbstractService {

	private static OperationTypeService thisInstance = new OperationTypeService();

	private OperationTypeDao operationTypeDao;

	public static OperationTypeService getInstance() {
		return thisInstance;
	}

	private OperationTypeService() {
		super("bank");
		operationTypeDao = new OperationTypeDao("bank");
	}

	public List<OperationType> findAllOperationTypes() {
		return operationTypeDao.findAll(null);
	}

	public GenericModelDto<OperationType> addOperationType(final GenericModelDto<OperationType> dto) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public GenericModelDto<OperationType> doWork(EntityManager em) throws Exception {

				if (dto == null) {
					throw new NullPointerException("dto is null.");
				}

				OperationType op = dto.asObject();

				if (op == null) {
					throw new MappingException("Cannot map dto with model.");
				}

				if (op.getId() != null) {
					throw new Exception("Cannot have Id.");
				}

				op = operationTypeDao.save(em, op);
				return new GenericModelDto<OperationType>(op);
			}
		});
	}

	public GenericModelDto<OperationType> updateOperationType(final GenericModelDto<OperationType> dto) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public GenericModelDto<OperationType> doWork(EntityManager em) throws Exception {

				OperationType op = dto.asObject();

				if (op.getId() == null) {
					throw new Exception("Cannot have empty id");
				}

				op = operationTypeDao.save(em, op);
				return new GenericModelDto<OperationType>(op);
			}
		});
	}

	public void deleteOperationType(final Integer id) throws Exception {

		this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public Object doWork(EntityManager em) throws Exception {

				operationTypeDao.delete(em, id);
				return null;
			}
		});
	}
}
