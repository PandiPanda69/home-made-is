package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import fr.thedestiny.Constants;
import fr.thedestiny.bank.models.Repetition;
import fr.thedestiny.global.dao.AbstractDao;

@Repository
public class RepetitionDao extends AbstractDao<Repetition> {

	private RepetitionDao() {
		super(Constants.BANK_CONTEXT);
	}

	public List<Repetition> findAll() {
		return em()
				.createQuery("FROM " + Repetition.class.getName(), Repetition.class)
				.getResultList();
	}

	public List<Repetition> findByAccount(final int account) {

		String query = new StringBuilder("FROM ")
		.append(Repetition.class.getName())
		.append(" WHERE compte.id = ?)")
		.toString();

		return em()
				.createQuery(query, Repetition.class)
				.setParameter(1, account)
				.getResultList();
	}

	public boolean delete(EntityManager em, final int id, final int accountId) {

		int result = em
				.createQuery("DELETE FROM Repetition WHERE id = ? AND compte.id = ?")
				.setParameter(1, id)
				.setParameter(2, accountId)
				.executeUpdate();

		return result == 1;
	}
}
