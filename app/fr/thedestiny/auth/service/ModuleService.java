package fr.thedestiny.auth.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.auth.dao.ModuleDao;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class ModuleService extends AbstractService {

	@Autowired
	private ModuleDao moduleDao;

	public List<Module> findAllModules(final int userId, final boolean isAdmin) {

		return this.processInTransaction(new InTransactionFunction() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Module> doWork(EntityManager em) {
				if (isAdmin) {
					return moduleDao.findAll(em);
				}

				return moduleDao.findModulesForUser(em, userId);
			}
		});
	}

	public Module saveModule(final Module module) {

		return this.processInTransaction(new InTransactionFunction() {

			@SuppressWarnings("unchecked")
			@Override
			public Module doWork(EntityManager em) {
				return moduleDao.add(module);
			}
		});
	}

	public boolean deleteModule(final int id) {
		return this.processInTransaction(new InTransactionFunction() {

			@SuppressWarnings("unchecked")
			@Override
			public Boolean doWork(EntityManager em) {
				return moduleDao.delete(id);
			}
		});
	}
}
