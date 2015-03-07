package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle OperationType
 * @author Sébastien
 */
@Repository
public class OperationTypeDao extends AbstractDao<OperationType> {

	private OperationTypeDao() {
		super("bank");
	}

	public OperationType findById(EntityManager em, final int id) {
		return findById(em, id, OperationType.class);
	}

	public List<OperationType> findAll(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}
		return em.createQuery("from OperationType order by name", OperationType.class).getResultList();
	}

	public void delete(EntityManager em, final OperationType op) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		em.remove(op);
	}

	public boolean delete(EntityManager em, final int id) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		int result = em.createQuery("delete from OperationType where id = ?").setParameter(1, id).executeUpdate();
		return result == 1;
	}
}
