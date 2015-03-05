package fr.thedestiny.home.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;

public class HomeController extends Controller {

	public static final String KEY_CACHED_INDEX = "cachedRPiHomeIndex";

	@Transactional(readOnly = true)
	@Security
	public static Result index() {

		UserDto user = SecurityHelper.getLoggedUser();
		return ok(fr.thedestiny.home.view.html.index.render(user));
	}
}
