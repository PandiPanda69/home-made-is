package fr.thedestiny.torrent.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.torrent.dto.HomeStatsDto;
import fr.thedestiny.torrent.service.StatsService;

@org.springframework.stereotype.Controller
public class StatsController extends Controller {

	@Autowired
	private StatsService statsService;

	@Transactional(readOnly = true)
	@Security
	public Result home() {

		HomeStatsDto dto = statsService.getStatsForHomepage();
		return ok(dto.toJson());
	}

}
