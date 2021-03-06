package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.Constants;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle Operation
 * @author Sébastien
 */
@Repository
public class OperationDao extends AbstractDao<Operation> {

	private OperationDao() {
		super(Constants.BANK_CONTEXT, Operation.class);
	}

	public List<Operation> findAll(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}
		return em.createQuery("from Operation", Operation.class).getResultList();
	}

	public List<Operation> findAll(EntityManager em, final int idAccount, final int idMois) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("from Operation where compte_id = ? and mois_id = ?", Operation.class)
				.setParameter(1, idAccount)
				.setParameter(2, idMois)
				.getResultList();
	}

	public boolean delete(EntityManager em, final int id) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		int result = em.createQuery("delete from Operation where id = ?").setParameter(1, id).executeUpdate();
		return result == 1;
	}

	public List<Operation> findOperationOfYear(final int accountId, final int year) {

		return JPA.em(persistenceContext).createQuery(
				"from Operation op " +
						"join fetch op.mois mois " +
						"join fetch op.compte compte " +
						"where compte.id = :accountId " +
						"and mois.annee = :year " +
						"order by mois.mois", Operation.class)
						.setParameter("accountId", accountId)
						.setParameter("year", year)
						.getResultList();
	}

	public List<Operation> findUserOperationsById(final List<Integer> ids, final int userId) {

		return JPA.em(persistenceContext).createQuery(
				"from Operation op " +
						"join fetch op.mois mois " +
						"join fetch op.compte compte " +
						"join fetch op.type type " +
						"where compte.owner = :user " +
						"and op.id in :ids " +
						"order by mois.annee desc, mois.mois desc", Operation.class)
				.setParameter("user", userId)
				.setParameter("ids", ids)
				.getResultList();
	}
}
