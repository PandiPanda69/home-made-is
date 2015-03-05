package fr.thedestiny.auth.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.auth.model.Module;

@Repository
public class ModuleDao {

	public List<Module> findAll(EntityManager em) {
		return em.createQuery("from Module", Module.class).getResultList();
	}

	public List<Module> findModulesForUser(EntityManager em, final int userId) {
		return em.createQuery("SELECT u.privileges FROM Utilisateur u  WHERE u.id = ?", Module.class)
				.setParameter(1, userId)
				.getResultList();
	}

	public Module findModuleById(final int id) {
		return JPA.em().find(Module.class, id);
	}

	public Module add(Module module) {
		return JPA.em().merge(module);
	}

	public boolean delete(final int id) {
		int result = JPA.em().createQuery("DELETE FROM Module WHERE id = ?")
				.setParameter(1, id)
				.executeUpdate();

		return (result == 1);
	}
}
