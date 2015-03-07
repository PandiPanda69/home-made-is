package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle MotifOperation
 * @author Sébastien
 */
@Repository
public class MotifOperationDao extends AbstractDao<MotifOperation> {

	private MotifOperationDao() {
		super("bank");
	}

	public List<MotifOperation> findAll(final int userId) {
		return JPA.em(persistenceContext).createQuery("from MotifOperation where userId = ?", MotifOperation.class)
				.setParameter(1, userId)
				.getResultList();
	}

	public MotifOperation findUnique(final int id) {
		try {
			return JPA.em(persistenceContext).createQuery("from MotifOperation where id = ?", MotifOperation.class)
					.setParameter(1, id)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public MotifOperation save(EntityManager em, final MotifOperation motif) {
		em.persist(motif);
		return motif;
	}

	public boolean delete(EntityManager em, final int userId, final int motifId) {
		int result = em.createQuery("delete from MotifOperation where id = :motifId and user_id = :userId")
				.setParameter("motifId", motifId)
				.setParameter("userId", userId)
				.executeUpdate();

		return result == 1;
	}
}
