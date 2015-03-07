package fr.thedestiny.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.auth.dao.ModuleDao;
import fr.thedestiny.auth.dao.UtilisateurDao;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class UserService extends AbstractService {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UtilisateurDao userDao;

	@Autowired
	private ModuleDao moduleDao;

	public UserDto findUserById(final int id) {

		Utilisateur u = processInTransaction(new InTransactionFunction<Utilisateur>() {

			@Override
			public Utilisateur doWork(EntityManager em) throws Exception {
				return userDao.findById(em, id);
			}
		});

		return new UserDto(u);
	}

	public List<UserDto> findAllUsers() {

		List<UserDto> userList = new ArrayList<>();

		List<Utilisateur> users = processInTransaction(new InTransactionFunction<List<Utilisateur>>() {

			@Override
			public List<Utilisateur> doWork(EntityManager em) throws Exception {
				return userDao.findAll(em);
			}
		});

		for (Utilisateur current : users) {
			userList.add(new UserDto(current));
		}

		return userList;
	}

	public UserDto saveNewUser(final UserDto dto) {
		Utilisateur u = new Utilisateur();
		return updateUser(dto, u);
	}

	public UserDto saveUser(final UserDto dto) {
		Utilisateur u = processInTransaction(new InTransactionFunction<Utilisateur>() {

			@Override
			public Utilisateur doWork(EntityManager em) throws Exception {
				return userDao.findById(em, dto.getId());
			}
		});
		return updateUser(dto, u);
	}

	private UserDto updateUser(final UserDto dto, final Utilisateur u) {

		Utilisateur saved = this.processInTransaction(new InTransactionFunction<Utilisateur>() {

			@Override
			public Utilisateur doWork(EntityManager em) {

				u.setUsername(dto.getUsername());
				if (dto.getPassword() != null && dto.getPassword().length() > 0) {
					u.setPassword(authenticationService.encodePassword(dto.getPassword()));
				}
				u.setIsAdmin(dto.isAdmin());
				u.setFirstName(dto.getFirstName());

				Set<Module> modules = new TreeSet<>();
				for (Integer moduleId : dto.getModules()) {
					modules.add(moduleDao.findModuleById(moduleId));
				}

				u.setPrivileges(modules);

				return userDao.save(em, u);
			}
		});

		return new UserDto(saved);
	}

	public boolean deleteUser(final int id) {
		return processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws Exception {
				return userDao.delete(em, id);
			}
		});
	}
}
