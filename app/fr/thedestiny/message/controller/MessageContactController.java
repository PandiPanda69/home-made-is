package fr.thedestiny.message.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.global.dto.GraphDto;
import fr.thedestiny.global.helper.ResultFactory;
import fr.thedestiny.message.model.Contact;
import fr.thedestiny.message.service.ContactService;
import fr.thedestiny.message.service.MessageStatService;

@org.springframework.stereotype.Controller
public class MessageContactController extends Controller {

	@Autowired
	private ContactService contactService;

	@Autowired
	private MessageStatService statService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	public Result list() {
		List<Contact> contacts = contactService.findContacts();
		return ok(Json.toJson(contacts));
	}

	@Security
	public Result edit(final Integer id) throws IOException {
		Contact contact = mapper.readValue(ctx().request().body().asJson().toString(), Contact.class);
		return ok(Json.toJson(contactService.updateContact(contact)));
	}

	@Security
	public Result delete(final Integer id) {
		contactService.deleteContact(id);
		return ResultFactory.OK;
	}

	@Security
	public Result stats(final Integer id) {
		GraphDto<List<Long>> dto = statService.computeStatsForContact(id);
		return ok(Json.toJson(dto));
	}
}
