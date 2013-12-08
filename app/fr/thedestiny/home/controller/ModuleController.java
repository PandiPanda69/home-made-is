package fr.thedestiny.home.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.auth.service.ModuleService;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class ModuleController extends Controller {

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

		GenericModelDto<Module> dto = null;
		try {
			dto = new GenericModelDto<Module>(ctx().request().body().asJson(), Module.class);

			dto = moduleService.saveModule(dto);

		} catch (Exception ex) {
			return ResultFactory.FAIL;
		}

		return ok(dto.asJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result edit(Integer id) {

		GenericModelDto<Module> dto = null;
		try {
			dto = new GenericModelDto<Module>(ctx().request().body().asJson(), Module.class);

			dto = moduleService.saveModule(dto);

		} catch (Exception ex) {
			return ResultFactory.FAIL;
		}

		return ok(dto.asJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result delete(Integer id) {

		moduleService.deleteModule(id);
		return ResultFactory.OK;
	}
}
