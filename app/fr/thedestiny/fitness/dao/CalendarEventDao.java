package fr.thedestiny.fitness.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.fitness.model.CalendarEvent;
import fr.thedestiny.global.dao.AbstractDao;

@Repository
public class CalendarEventDao extends AbstractDao<CalendarEvent> {

	public CalendarEventDao() {
		super("fitness");
	}

	@SuppressWarnings("unchecked")
	public List<CalendarEvent> findAll(final int userId) {

		return JPA.em(this.persistenceContext)
				.createQuery("from CalendarEvent where userId = ? order by date")
				.setParameter(1, userId)
				.getResultList();
	}

	public List<CalendarEvent> findForMonth(final int userId, final int month, final int year) {

		Calendar min = new GregorianCalendar(year, month, 1);
		Calendar max = new GregorianCalendar(year, month + 1, 0);

		return JPA.em(this.persistenceContext)
				.createQuery("FROM CalendarEvent WHERE  userId = :userId AND date BETWEEN :min AND :max ORDER BY date", CalendarEvent.class)
				.setParameter("userId", userId)
				.setParameter("min", min.getTime())
				.setParameter("max", max.getTime())
				.getResultList();
	}

	public boolean deleteCalendarEvent(EntityManager em, final int id) {
		int result = em.createQuery("DELETE FROM CalendarEvent WHERE id = ?")
				.setParameter(1, id)
				.executeUpdate();

		return (result == 1);
	}
}
