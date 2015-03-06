package fr.thedestiny.message.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

import fr.thedestiny.message.service.KeyVerifierService;

@org.springframework.stereotype.Controller
public class MessageSynchronizerController extends Controller {

	private static final String HEADER_KEY = "sms-backup-key";

	@Autowired
	private KeyVerifierService keyService;

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

		/* ******************************************************************** */
		/* TODO : Handle data. */
		/* ******************************************************************** */
		long timestamp = System.currentTimeMillis();

		try (FileOutputStream out = new FileOutputStream("output_" + timestamp)) {
			out.write(body.toString().getBytes("UTF-8"));
		} catch (IOException ex) {
			Logger.error("Error while saving request body.", ex);
			return internalServerError();
		}

		return ok(String.valueOf(body.size()));
	}
}
