package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Compte
 * @author Sébastien
 */
@Repository
public class SoldeDao extends AbstractDao<Solde> {

	private SoldeDao() {
		super("bank");
	}

	public List<Solde> findAll(EntityManager em, final int compteId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Solde where compte_id = ? order by mois_id", Solde.class)
				.setParameter(1, compteId)
				.getResultList();
	}

	public Solde find(EntityManager em, final int compteId, final int moisId) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		try {
			return em.createQuery("from Solde where compte_id = ? AND mois_id = ?", Solde.class)
					.setParameter(1, compteId)
					.setParameter(2, moisId)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Solde persist(EntityManager em, final Solde solde) {
		em.persist(solde);
		return solde;
	}

	public void delete(EntityManager em, final Solde solde) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		em.remove(solde);
	}
}
