package fr.thedestiny;

import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class Application extends Controller {

	public Result options(final String path) {
		return ok();
	}
}