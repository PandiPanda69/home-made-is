package fr.thedestiny.fitness.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.Constants;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.fitness.dto.StatsDto;
import fr.thedestiny.fitness.service.StatService;

@org.springframework.stereotype.Controller("FitnessHomeController")
public class HomeController extends Controller {

	@Autowired
	private StatService statService;

	@Security
	public Result index() {
		return ok(fr.thedestiny.fitness.view.html.index.render(Constants.FITNESS_VERSION));
	}

	@Security
	public Result stats() {

		StatsDto dto = statService.computeWeightStat(SecurityHelper.getLoggedUserId());
		return ok(dto.toJson());
	}
}
