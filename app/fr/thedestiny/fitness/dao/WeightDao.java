package fr.thedestiny.fitness.dao;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.fitness.model.CalendarEvent;
import fr.thedestiny.fitness.model.Weight;
import fr.thedestiny.global.dao.AbstractDao;

@Repository
public class WeightDao extends AbstractDao<Weight> {

	public WeightDao() {
		super("fitness");
	}

	public Weight findWeightForEvent(CalendarEvent event) {
		return (Weight) JPA.em(this.persistenceContext)
				.createQuery("from Weight where calendarEvent = :event")
				.setParameter("event", event)
				.getSingleResult();
	}

	public void deleteWeight(EntityManager em, Integer id) {
		em.createQuery("delete from Weight where id = ?")
				.setParameter(1, id)
				.executeUpdate();
	}
}
