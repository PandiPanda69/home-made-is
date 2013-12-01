package fr.thedestiny.auth.controller;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.Constants;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.auth.service.AuthenticationService;

@org.springframework.stereotype.Controller
public class AuthenticationController extends Controller {

	@Autowired
	private AuthenticationService authenticationService;

	public AuthenticationController() {
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
}
