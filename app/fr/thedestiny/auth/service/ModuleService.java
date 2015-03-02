package fr.thedestiny.auth.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.auth.dao.ModuleDao;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class ModuleService extends AbstractService {

	@Autowired
	private ModuleDao moduleDao;

	public List<Module> findAllModules(final Integer userId, final boolean isAdmin) throws Exception {

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

	public GenericModelDto<Module> saveModule(GenericModelDto<Module> dto) {

		Module module = dto.asObject();
		return new GenericModelDto<Module>(moduleDao.add(module));
	}

	public void deleteModule(Integer id) {
		moduleDao.delete(id);
	}
}
