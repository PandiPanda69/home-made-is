package fr.thedestiny.home.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.model.Module;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.auth.service.ModuleService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class ModuleController extends Controller {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ModuleService moduleService;

	@Transactional(readOnly = true)
	@Security
	public Result list() {

		List<Module> modules = moduleService.findAllModules(SecurityHelper.getLoggedUserId(), SecurityHelper.isAdmin());
		return ok(Json.toJson(modules));
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result add() {

		Module module = null;
		try {
			module = objectMapper.readValue(ctx().request().body().asJson().toString(), Module.class);
			module = moduleService.saveModule(module);
		} catch (IOException ex) {
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(module));
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result edit(final Integer id) {

		Module module = null;
		try {
			module = objectMapper.readValue(ctx().request().body().asJson().toString(), Module.class);
			module = moduleService.saveModule(module);
		} catch (IOException ex) {
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(module));
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result delete(final Integer id) {

		if (!moduleService.deleteModule(id)) {
			return ResultFactory.FAIL;
		}
		return ResultFactory.OK;
	}
}
