package fr.thedestiny.auth.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.auth.model.Utilisateur;

/**
 * Data Access object pour le modèle Utilisateur
 * @author Sébastien
 */
@Repository
public class UtilisateurDao {

	@PersistenceContext
	private EntityManager em;

	public UtilisateurDao() {
	}

	public Utilisateur findById(Integer id) {
		return em().find(Utilisateur.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Utilisateur> findAll() {
		return em().createQuery("from Utilisateur").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Utilisateur> findByUsername(String username) {
		return em().createQuery("from Utilisateur where username like ?").setParameter(1, username).getResultList();
	}

	public Utilisateur save(Utilisateur u) {

		if (u.getId() != null) {
			return em().merge(u);
		}

		em().persist(u);
		return u;
	}

	public void delete(Utilisateur u) {
		em().remove(u);
	}

	public void delete(Integer id) throws Exception {
		int result = em().createQuery("delete from Utilisateur where id = ?").setParameter(1, id).executeUpdate();
		if (result == 0) {
			throw new Exception("Failure");
		}
	}

	private EntityManager em() {
		try {
			return JPA.em();
		} catch (Exception ex) {
			return this.em;
		}
	}
}
