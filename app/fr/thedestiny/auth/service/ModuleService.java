package fr.thedestiny.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.auth.dao.ModuleDao;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.global.dto.GenericModelDto;

@Service
public class ModuleService {

	@Autowired
	private ModuleDao moduleDao;

	public List<Module> findAllModules(Integer userId, boolean isAdmin) {
		if (isAdmin) {
			return moduleDao.findAll();
		}

		return moduleDao.findModulesForUser(userId);
	}

	public GenericModelDto<Module> saveModule(GenericModelDto<Module> dto) {

		Module module = dto.asObject();
		return new GenericModelDto<Module>(moduleDao.add(module));
	}

	public void deleteModule(Integer id) {
		moduleDao.delete(id);
	}
}
