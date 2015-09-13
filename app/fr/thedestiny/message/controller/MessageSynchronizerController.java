package fr.thedestiny.message.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.message.service.KeyVerifierService;
import fr.thedestiny.message.service.MessageService;

@org.springframework.stereotype.Controller
public class MessageSynchronizerController extends Controller {

	private static final String HEADER_KEY = "sms-backup-key";

	@Autowired
	private KeyVerifierService keyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private ObjectMapper mapper;

	@Transactional
	public Result synchronize() {

		Request req = ctx().request();

		Map<String, String[]> headers = req.headers();
		String[] headerKey = headers.get(HEADER_KEY);

		if (headerKey == null || headerKey.length != 1 || !keyService.isKeyValid(headerKey[0])) {
			return forbidden();
		}

		JsonNode body = req.body().asJson();
		if (body == null || body.size() == 0) {
			return badRequest("Empty request.");
		}

		try {
			@SuppressWarnings("unchecked")
			List<HashMap<String, Object>> messages = mapper.readValue(body.toString(), List.class);
			messageService.pushMessages(messages);

			return ok(String.valueOf(messages.size()));
		} catch (IOException ex) {
			Logger.error("Error while sync:", ex);
			return internalServerError();
		}
	}
}