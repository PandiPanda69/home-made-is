package fr.thedestiny.torrent.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.torrent.dto.HomeStatsDto;
import fr.thedestiny.torrent.service.StatsService;

public class StatsController extends Controller {

	private static StatsService statsService = StatsService.getInstance();

	@Transactional(readOnly = true)
	@Security
	public static Result home() {

		HomeStatsDto dto = statsService.getStatsForHomepage();
		return ok(dto.toJson());
	}

}
