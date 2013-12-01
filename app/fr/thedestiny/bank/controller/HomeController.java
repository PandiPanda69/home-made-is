package fr.thedestiny.bank.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;

public class HomeController extends Controller {

	public static final String KEY_CACHED_INDEX = "cachedRPiBankIndex";

	@Transactional(readOnly = true)
	@Security
	public static Result index() {

		UserDto user = SecurityHelper.getLoggedUser();
		if (user == null) {
			//			return AuthenticationController.index();
		}

		//		Result cachedIndex = (Result) Cache.get(KEY_CACHED_INDEX);
		//		if (cachedIndex == null) {
		//			cachedIndex = ok(fr.thedestiny.bank.views.html.index.render(user));
		//			Cache.set(KEY_CACHED_INDEX, cachedIndex);
		//		}
		//
		//		return cachedIndex;

		return ok(fr.thedestiny.bank.views.html.index.render(user));
	}
}
