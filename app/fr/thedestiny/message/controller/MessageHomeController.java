package fr.thedestiny.message.controller;

import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;

@org.springframework.stereotype.Controller
public class MessageHomeController extends Controller {

	@Security
	public Result index() {
		UserDto user = SecurityHelper.getLoggedUser();
		return ok(fr.thedestiny.message.view.html.index.render(user));
	}
}
