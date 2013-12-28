package fr.thedestiny.fitness.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.fitness.dao.CalendarEventDao;
import fr.thedestiny.fitness.dto.StatsDto;
import fr.thedestiny.fitness.model.CalendarEvent;
import fr.thedestiny.global.service.AbstractService;

@Service
public class StatService extends AbstractService {

	@Autowired
	private CalendarEventDao calendarDao;

	public StatService() {
		super("fitness");
	}

	public StatsDto computeWeightStat(Integer userId) {

		List<CalendarEvent> events = calendarDao.findAll(userId);
		StatsDto dto = new StatsDto();

		for (CalendarEvent current : events) {
			dto.addStat(current.getDate().getTime(), current.getWeight().getValue());
		}

		return dto;
	}
}
