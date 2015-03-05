package fr.thedestiny.auth.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import fr.thedestiny.auth.model.Utilisateur;

/**
 * Data Access object pour le modèle Utilisateur
 * @author Sébastien
 */
@Repository
public class UtilisateurDao {

	private UtilisateurDao() {
	}

	public Utilisateur findById(EntityManager em, final int id) {
		return em.find(Utilisateur.class, id);
	}

	public List<Utilisateur> findAll(EntityManager em) {
		return em.createQuery("from Utilisateur", Utilisateur.class).getResultList();
	}

	public List<Utilisateur> findByUsername(EntityManager em, final String username) {
		return em.createQuery("from Utilisateur where username like ?", Utilisateur.class)
				.setParameter(1, username)
				.getResultList();
	}

	public Utilisateur save(EntityManager em, Utilisateur u) {

		if (u.getId() != null) {
			return em.merge(u);
		}

		em.persist(u);
		return u;
	}

	public boolean delete(EntityManager em, final int id) {
		int result = em.createQuery("delete from Utilisateur where id = ?").setParameter(1, id).executeUpdate();
		return (result != 0);
	}
}
