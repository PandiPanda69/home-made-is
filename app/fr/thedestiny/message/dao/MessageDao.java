package fr.thedestiny.message.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import fr.thedestiny.Constants;
import fr.thedestiny.global.dao.AbstractDao;
import fr.thedestiny.message.model.Message;

@Repository
public class MessageDao extends AbstractDao<Message> {

	private MessageDao() {
		super(Constants.MESSAGES_CONTEXT, Message.class);
	}

	@Override
	public List<Message> findAll() {
		return em()
				.createQuery("FROM Message ORDER BY date", Message.class)
				.getResultList();
	}

	public List<?> findCountByContact() {
		return em().createQuery(
				"SELECT c.id, c.name, count(m.id), max(m.date) " +
						"FROM Contact c, Message m " +
						"JOIN c.phones cp " +
						"JOIN m.phone mp " +
						"WHERE mp.id = cp.id " +
						"GROUP BY c.id, c.name " +
						"ORDER BY max(m.date) DESC"
				)
				.getResultList();
	}

	public List<Message> findByContact(final int id) {
		return em().createQuery(
				"SELECT m " +
						"FROM Contact c, Message m " +
						"JOIN c.phones cp " +
						"JOIN FETCH m.phone mp " +
						"WHERE mp.id = cp.id AND c.id = ? " +
						"ORDER BY m.date DESC",
						Message.class
				)
				.setParameter(1, id)
				.getResultList();
	}
}
