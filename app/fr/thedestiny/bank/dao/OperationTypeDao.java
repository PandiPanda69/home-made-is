package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle OperationType
 * @author Sébastien
 */
public class OperationTypeDao extends AbstractDao<OperationType> {

	public OperationTypeDao(String persistenceContext) {
		super(persistenceContext);
	}

	public OperationType findById(EntityManager em, Integer id) {
		return findById(em, id, OperationType.class);
	}

	@SuppressWarnings("unchecked")
	public List<OperationType> findAll(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}
		return em.createQuery("from OperationType order by name").getResultList();
	}

	public void delete(EntityManager em, OperationType op) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		em.remove(op);
	}

	public void delete(EntityManager em, Integer id) throws Exception {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		int result = em.createQuery("delete from OperationType where id = ?").setParameter(1, id).executeUpdate();
		if (result == 0) {
			throw new Exception("Failure");
		}
	}
}
