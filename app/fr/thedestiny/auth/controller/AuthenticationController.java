package fr.thedestiny.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.thedestiny.Constants;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.auth.service.AuthenticationService;

@org.springframework.stereotype.Controller
public class AuthenticationController extends Controller {

	@Autowired
	private AuthenticationService authenticationService;

	private AuthenticationController() {
	}

	public Result index() {
		return ok(fr.thedestiny.auth.view.html.authentication.render());
	}

	public Result notGranted() {
		return forbidden();
	}

	@Transactional(readOnly = true)
	public Result authenticate() {

		ObjectNode res = JsonNodeFactory.instance.objectNode();
		JsonNode node = ctx().request().body().asJson();

		String username = node.get("username").asText();
		String password = node.get("password").asText();

		Utilisateur user = authenticationService.authenticate(username, password);

		if (user == null) {
			res.put(Constants.JSON_RESULT_CODE, "fail");
			return forbidden(res);
		}

		SecurityHelper.setUserSession(user);

		res.put(Constants.JSON_RESULT_CODE, "ok");
		return ok(res);
	}

	public Result logout() {
		SecurityHelper.setUserSession(null);
		return index();
	}

	@Transactional(readOnly = true)
	public Result authenticateApi() {
		ObjectNode res = JsonNodeFactory.instance.objectNode();
		JsonNode node = ctx().request().body().asJson();

		if (node.get("username") == null || node.get("password") == null) {
			res.put(Constants.JSON_RESULT_CODE, "fail");
			res.put("msg", "Empty username/password.");
			return forbidden(res);
		}

		String username = node.get("username").asText();
		String password = node.get("password").asText();

		Utilisateur user = authenticationService.authenticate(username, password);

		if (user == null) {
			res.put(Constants.JSON_RESULT_CODE, "fail");
			res.put("msg", "Bad credentials.");
			return forbidden(res);
		}

		String token = SecurityHelper.createUserToken(user);
		res.put(Constants.JSON_RESULT_CODE, "ok");
		res.put("token", token);
		return ok(res);
	}

	public Result logoutApi() {
		SecurityHelper.removeUserToken();

		ObjectNode res = JsonNodeFactory.instance.objectNode();
		res.put(Constants.JSON_RESULT_CODE, "ok");
		return ok(res);
	}

	@Security
	public Result currentUser() {
		UserDto dto = SecurityHelper.getLoggedUser();
		return ok(Json.toJson(dto));
	}
}
