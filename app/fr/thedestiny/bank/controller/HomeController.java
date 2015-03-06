package fr.thedestiny.bank.controller;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;

@org.springframework.stereotype.Controller("BankHomeController")
public class HomeController extends Controller {

	@Transactional(readOnly = true)
	@Security
	public Result index() {

		UserDto user = SecurityHelper.getLoggedUser();
		return ok(fr.thedestiny.bank.views.html.index.render(user));
	}
}
