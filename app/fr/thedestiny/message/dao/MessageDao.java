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

	public int countMessage() {
		return em().createQuery("select count(*) from Message", Long.class)
				.getSingleResult()
				.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> countMessageByDay() {
		return em()
				.createNativeQuery("select strftime('%Y-%m-%d', dat_msg/1000, 'unixepoch'), count(*) from Message group by 1 order by 1")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> countMessageByDayForContact(int id) {
		return em()
				.createNativeQuery("select strftime('%Y-%m-%d', dat_msg/1000, 'unixepoch'), count(*) from Message m"
						+ " inner join Contact_Phone cp on cp.id = m.phone_id"
						+ " where cp.contact_id = :contact group by 1 order by 1")
						.setParameter("contact", id)
						.getResultList();
	}
}
