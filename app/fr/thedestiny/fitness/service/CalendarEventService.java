package fr.thedestiny.fitness.service;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.fitness.dao.CalendarEventDao;
import fr.thedestiny.fitness.dao.WeightDao;
import fr.thedestiny.fitness.dto.CalendarEventDto;
import fr.thedestiny.fitness.model.CalendarEvent;
import fr.thedestiny.fitness.model.Weight;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class CalendarEventService extends AbstractService {

	@Autowired
	private CalendarEventDao calendarDao;

	@Autowired
	private WeightDao weightDao;

	public CalendarEventService() {
		super("fitness");
	}

	public List<CalendarEventDto> findAllCalendarEventsForUser(final int userId) {
		List<CalendarEvent> events = calendarDao.findAll(userId);
		return convert(events);
	}

	public List<CalendarEventDto> findCalendarEventsForUserForMonth(final int userId, final int month, final int year) {

		List<CalendarEvent> events = calendarDao.findForMonth(userId, month, year);
		return convert(events);
	}

	public CalendarEventDto saveCalendarEvent(final CalendarEventDto dto, final int userId) throws Exception {

		return this.processInTransaction(new InTransactionFunction<CalendarEventDto>() {

			@Override
			public CalendarEventDto doWork(EntityManager em) {

				// Don't add empty event.
				if (dto.getWeight() == null) {
					throw new InvalidParameterException();
				}

				CalendarEvent event = new CalendarEvent();
				event.setId(dto.getId());
				event.setDate(new Date(dto.getTimestamp()));
				event.setUserId(userId);

				Weight weight = new Weight();
				weight.setValue(dto.getWeight());
				weight.setCalendarEvent(event);

				em.persist(weight);

				return new CalendarEventDto(event);
			}
		});
	}

	public CalendarEventDto updateCalendarEvent(final CalendarEventDto dto, final int userId) throws Exception {

		return this.processInTransaction(new InTransactionFunction<CalendarEventDto>() {

			@Override
			public CalendarEventDto doWork(EntityManager em) throws Exception {

				CalendarEvent event = new CalendarEvent();
				event.setId(dto.getId());
				event.setDate(new Date(dto.getTimestamp()));
				event.setUserId(userId);

				Weight currentWeight = weightDao.findWeightForEvent(event);

				// Delete !
				if (dto.getWeight() == null || dto.getWeight().isNaN()) {
					weightDao.deleteWeight(em, currentWeight.getId());
					calendarDao.deleteCalendarEvent(em, event.getId());
					return null;
				}

				currentWeight.setValue(dto.getWeight());
				em.merge(currentWeight);

				return dto;
			}
		});
	}

	private List<CalendarEventDto> convert(final List<CalendarEvent> events) {

		List<CalendarEventDto> dto = new ArrayList<>(events.size());
		for (CalendarEvent current : events) {
			dto.add(new CalendarEventDto(current));
		}

		return dto;
	}
}
