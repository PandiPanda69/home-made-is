package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Compte
 * @author Sébastien
 */
public class CompteDao extends AbstractDao<Compte> {

	public CompteDao(String persistenceContext) {
		super(persistenceContext);
	}

	public Compte findById(EntityManager em, Integer id) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.find(Compte.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Compte> findAll(EntityManager em, Integer ownerId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Compte where owner_id = ?").setParameter(1, ownerId).getResultList();
	}

	public boolean delete(EntityManager em, Integer id) {
		int result = em.createQuery("delete from Compte where id = ?").setParameter(1, id).executeUpdate();
		return result == 1;
	}
}
