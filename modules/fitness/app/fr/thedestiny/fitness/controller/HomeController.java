package fr.thedestiny.fitness.controller;

import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class HomeController extends Controller {

	public Result index() {
		return ok(fr.thedestiny.fitness.view.html.index.render("1.0"));
	}
}
