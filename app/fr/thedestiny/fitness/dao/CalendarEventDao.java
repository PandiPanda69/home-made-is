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
	public List<CalendarEvent> findAll(Integer userId) {

		return JPA.em(this.persistenceContext).createQuery("from CalendarEvent where userId = :userId order by date")
				.setParameter("userId", userId)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CalendarEvent> findForMonth(Integer userId, Integer month, Integer year) {

		Calendar min = new GregorianCalendar(year, month, 1);
		Calendar max = new GregorianCalendar(year, month + 1, 0);

		return JPA.em(this.persistenceContext)
				.createQuery("FROM CalendarEvent WHERE  userId = :userId AND date BETWEEN :min AND :max ORDER BY date")
				.setParameter("userId", userId)
				.setParameter("min", min.getTime())
				.setParameter("max", max.getTime())
				.getResultList();
	}

	public void deleteCalendarEvent(EntityManager em, Integer id) {
		em.createQuery("DELETE FROM CalendarEvent WHERE id = ?")
				.setParameter(1, id)
				.executeUpdate();
	}
}
