package fr.thedestiny.bank.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle MoisAnnee
 * @author Sébastien
 */
public class MoisAnneeDao extends AbstractDao<MoisAnnee> {

	public MoisAnneeDao(String persistenceContext) {
		super(persistenceContext);
	}

	public MoisAnnee findById(EntityManager em, Integer id) {
		return findById(em, id, MoisAnnee.class);
	}

	//	@SuppressWarnings("unchecked")
	//	public static List<MoisAnnee> findAll(Integer compteId) {
	//		return JPA.em().createNativeQuery("SELECT id, mois, annee FROM MoisAnnee WHERE id IN (SELECT DISTINCT mois_id FROM Operation WHERE compte_id = ? ORDER BY mois_id)", MoisAnnee.class).setParameter(1, compteId).getResultList();
	//	}

	public MoisAnnee findUnique(EntityManager em, Integer mois, Integer annee) {
		try {
			if (em == null) {
				em = JPA.em(persistenceContext);
			}

			return (MoisAnnee) em.createQuery("from MoisAnnee where mois = ? and annee = ? order by annee, mois").setParameter(1, mois).setParameter(2, annee).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
