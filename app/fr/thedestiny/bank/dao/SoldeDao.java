package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Compte
 * @author Sébastien
 */
public class SoldeDao extends AbstractDao<Solde> {

	public SoldeDao(String persistenceContext) {
		super(persistenceContext);
	}

	@SuppressWarnings("unchecked")
	public List<Solde> findAll(EntityManager em, Integer compteId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Solde where compte_id = ? order by mois_id").setParameter(1, compteId).getResultList();
	}

	public Solde find(EntityManager em, Integer compteId, Integer moisId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		try {
			return (Solde) em.createQuery("from Solde where compte_id = ? AND mois_id = ?").setParameter(1, compteId).setParameter(2, moisId).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Solde persist(EntityManager em, Solde solde) {
		em.persist(solde);
		return solde;
	}

	public void delete(EntityManager em, Solde solde) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		em.remove(solde);
	}
}
