package fr.thedestiny.message.controller;

import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;

@org.springframework.stereotype.Controller
public class MessageHomeController extends Controller {

	@Security
	public Result index() {
		return TODO;
	}
}
