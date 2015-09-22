package fr.thedestiny.message.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.message.dto.MessageStatDto;
import fr.thedestiny.message.service.MessageStatService;

@org.springframework.stereotype.Controller
public class MessageHomeController extends Controller {

	@Autowired
	private MessageStatService statService;

	@Security
	public Result index() {
		UserDto user = SecurityHelper.getLoggedUser();
		return ok(fr.thedestiny.message.view.html.index.render(user));
	}

	@Security
	public Result stats() {

		MessageStatDto dto = statService.computeStats();
		return ok(Json.toJson(dto));
	}
}
