package fr.thedestiny.bank.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.global.helper.ResultFactory;

public class HeuristiqueTypeController extends Controller {

	@Security
	@Transactional(readOnly = true)
	public static Result list() {

		//		List<HeuristiqueType> types = HeuristiqueTypeDao.findAll();
		//		return ok(Json.toJson(types));
		return ok("[]");
	}

	@Security
	@Transactional
	public static Result compute() {

		//		HeuristiqueTypeDelegate.compute();
		return ResultFactory.OK;
	}
}
