package fr.thedestiny.auth.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.auth.model.Module;

@Repository
public class ModuleDao {

	@SuppressWarnings("unchecked")
	public List<Module> findAll(EntityManager em) {
		return em.createQuery("from Module").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Module> findModulesForUser(EntityManager em, final Integer userId) {
		return em.createQuery("SELECT u.privileges FROM Utilisateur u  WHERE u.id = :userId").setParameter("userId", userId).getResultList();
	}

	public Module findModuleById(Integer id) {
		return JPA.em().find(Module.class, id);
	}

	public Module add(Module module) {
		return JPA.em().merge(module);
	}

	public void delete(Integer id) {
		JPA.em().createQuery("DELETE FROM Module WHERE id = :moduleId").setParameter("moduleId", id).executeUpdate();
	}
}
