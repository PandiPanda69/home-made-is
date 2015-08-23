package fr.thedestiny.message.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.message.service.MessageService;

@org.springframework.stereotype.Controller
public class MessageController extends Controller {

	@Autowired
	private MessageService messageService;

	public Result list() {
		return ok(Json.toJson(messageService.findCountByContact()));
	}

	public Result listByContact(final Integer id) {
		return ok(Json.toJson(messageService.findByContact(id)));
	}
}
