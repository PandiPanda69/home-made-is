package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle MotifOperation
 * @author Sébastien
 */
public class MotifOperationDao extends AbstractDao<MotifOperation> {

	public MotifOperationDao(String persistenceContext) {
		super(persistenceContext);
	}

	@SuppressWarnings("unchecked")
	public List<MotifOperation> findAll(Integer userId) {
		return JPA.em(persistenceContext).createQuery("from MotifOperation where userId = ?").setParameter(1, userId).getResultList();
	}

	public MotifOperation findUnique(Integer id) {
		try {
			return (MotifOperation) JPA.em(persistenceContext).createQuery("from MotifOperation where id = ?").setParameter(1, id).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public MotifOperation save(EntityManager em, MotifOperation motif) {
		em.persist(motif);
		return motif;
	}

	public void delete(EntityManager em, Integer userId, Integer motifId) throws Exception {
		int result = em.createQuery("delete from MotifOperation where id = :motifId and user_id = :userId").setParameter("motifId", motifId).setParameter("userId", userId).executeUpdate();
		if (result == 0) {
			throw new Exception("Failed to remove MotifOperation");
		}
	}
}
