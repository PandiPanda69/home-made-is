package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Operation
 * @author Sébastien
 */
public class OperationDao extends AbstractDao<Operation> {

	public OperationDao(String persistenceContext) {
		super(persistenceContext);
	}

	@SuppressWarnings("unchecked")
	public List<Operation> findAll(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}
		return em.createQuery("from Operation").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Operation> findAll(EntityManager em, Integer idAccount, Integer idMois) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Operation where compte_id = ? and mois_id = ?").setParameter(1, idAccount).setParameter(2, idMois).getResultList();
	}

	public Operation findById(EntityManager em, Integer id) {
		return findById(em, id, Operation.class);
	}

	public void delete(EntityManager em, Integer id) throws Exception {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		int result = em.createQuery("delete from Operation where id = ?").setParameter(1, id).executeUpdate();
		if (result == 0) {
			throw new Exception("Failure");
		}
	}
}
