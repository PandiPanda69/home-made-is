package fr.thedestiny.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.thedestiny.auth.dao.ModuleDao;
import fr.thedestiny.auth.dao.UtilisateurDao;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.global.config.SpringConfiguration;

@Service
public class UserService {

	private static UserService thisInstance = new UserService();

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UtilisateurDao userDao;

	@Autowired
	private ModuleDao moduleDao;

	public static UserService getInstance() {
		if (thisInstance.userDao == null) {
			thisInstance.userDao = SpringConfiguration.appContext.getBean(UtilisateurDao.class);
		}
		if (thisInstance.moduleDao == null) {
			thisInstance.moduleDao = SpringConfiguration.appContext.getBean(ModuleDao.class);
		}
		if (thisInstance.authenticationService == null) {
			thisInstance.authenticationService = SpringConfiguration.appContext.getBean(AuthenticationService.class);
		}

		return thisInstance;
	}

	public UserDto findUserById(Integer id) {
		return new UserDto(userDao.findById(id));
	}

	public List<UserDto> findAllUsers() {

		List<UserDto> userList = new ArrayList<UserDto>();

		for (Utilisateur current : userDao.findAll()) {
			userList.add(new UserDto(current));
		}

		return userList;
	}

	public UserDto saveNewUser(UserDto dto) {
		Utilisateur u = new Utilisateur();

		u.setUsername(dto.getUsername());
		u.setPassword(authenticationService.encodePassword(dto.getPassword()));
		u.setFirstName(dto.getFirstName());
		u.setIsAdmin(dto.isAdmin());

		Set<Module> modules = new TreeSet<Module>();
		for (Integer moduleId : dto.getModules()) {
			modules.add(moduleDao.findModuleById(moduleId));
		}

		u.setPrivileges(modules);

		return new UserDto(userDao.save(u));
	}

	@Transactional
	public UserDto saveUser(UserDto dto) {

		Utilisateur u = userDao.findById(dto.getId());

		u.setUsername(dto.getUsername());
		if (dto.getPassword() != null && dto.getPassword().length() > 0) {
			u.setPassword(authenticationService.encodePassword(dto.getPassword()));
		}
		u.setFirstName(dto.getFirstName());
		u.setIsAdmin(dto.isAdmin());

		Set<Module> modules = new TreeSet<Module>();
		for (Integer moduleId : dto.getModules()) {
			modules.add(moduleDao.findModuleById(moduleId));
		}

		u.setPrivileges(modules);

		return new UserDto(userDao.save(u));
	}

	public void deleteUser(Integer id) throws Exception {
		userDao.delete(id);
	}
}
