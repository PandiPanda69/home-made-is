package fr.thedestiny.fitness.controller;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.fitness.dto.CalendarEventDto;
import fr.thedestiny.fitness.service.CalendarEventService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class CalendarEventController extends Controller {

	@Autowired
	private CalendarEventService calendarService;

	@Autowired
	private ObjectMapper objectMapper;

	@Security
	public Result list(Integer month, Integer year) {

		List<CalendarEventDto> events = calendarService.findCalendarEventsForUserForMonth(SecurityHelper.getLoggedUserId(), month, year);
		return ok(Json.toJson(events));
	}

	@Transactional
	@Security
	public Result add() throws Exception {

		CalendarEventDto dto = objectMapper.convertValue(ctx().request().body().asJson(), CalendarEventDto.class);
		dto = calendarService.saveCalendarEvent(dto, SecurityHelper.getLoggedUserId());

		return ok(Json.toJson(dto));
	}

	@Transactional
	@Security
	public Result edit(Integer eventId) throws Exception {

		CalendarEventDto dto = objectMapper.convertValue(ctx().request().body().asJson(), CalendarEventDto.class);
		dto = calendarService.updateCalendarEvent(dto, SecurityHelper.getLoggedUserId());

		// A removal occured
		if (dto == null) {
			return ResultFactory.OK;
		}

		return ok(Json.toJson(dto));
	}
}
