package fr.thedestiny.bank.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.Constants;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle MoisAnnee
 * @author Sébastien
 */
@Repository
public class MoisAnneeDao extends AbstractDao<MoisAnnee> {

	private MoisAnneeDao() {
		super(Constants.BANK_CONTEXT, MoisAnnee.class);
	}

	public MoisAnnee findUnique(EntityManager em, final int mois, final int annee) {
		try {
			if (em == null) {
				em = JPA.em(persistenceContext);
			}

			return em.createQuery("from MoisAnnee where mois = ? and annee = ? order by annee, mois", MoisAnnee.class)
					.setParameter(1, mois)
					.setParameter(2, annee)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
