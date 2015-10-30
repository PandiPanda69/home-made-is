package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Compte
 * @author Sébastien
 */
@Repository
public class CompteDao extends AbstractDao<Compte> {

	private CompteDao() {
		super("bank", Compte.class);
	}

	public List<Compte> findAll(EntityManager em, final int ownerId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Compte where owner_id = ?", Compte.class)
				.setParameter(1, ownerId)
				.getResultList();
	}

	public boolean delete(EntityManager em, final int id) {
		int result = em.createQuery("delete from Compte where id = ?").setParameter(1, id).executeUpdate();
		return result == 1;
	}
}
