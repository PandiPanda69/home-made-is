package fr.thedestiny.bank.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class HeuristiqueTypeController extends Controller {

	@Security
	@Transactional(readOnly = true)
	public Result list() {

		//		List<HeuristiqueType> types = HeuristiqueTypeDao.findAll();
		//		return ok(Json.toJson(types));
		return ok("[]");
	}

	@Security
	@Transactional
	public Result compute() {

		//		HeuristiqueTypeDelegate.compute();
		return ResultFactory.OK;
	}
}
