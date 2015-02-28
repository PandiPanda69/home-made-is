package fr.thedestiny.home.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.service.UserService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class UtilisateurController extends Controller {

	@Autowired
	private UserService userService;

	private static ObjectMapper mapper = new ObjectMapper();

	@Transactional(readOnly = true)
	@Security(restrictedAccess = true)
	public Result list() {

		List<UserDto> list = userService.findAllUsers();
		return ok(Json.toJson(list));
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result add() {

		UserDto dto = null;
		try {
			dto = mapper.readValue(ctx().request().body().asJson().toString(), UserDto.class);

			if (dto.getId() != null) {
				throw new Exception("Cannot have Id.");
			}

			dto = userService.saveNewUser(dto);

		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result edit(Integer id) {

		UserDto dto = null;
		try {
			dto = mapper.readValue(ctx().request().body().asJson().toString(), UserDto.class);

			if (dto.getId() == null) {
				throw new Exception("Cannot have empty id");
			}

			dto = userService.saveUser(dto);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result delete(Integer id) {

		try {
			userService.deleteUser(id);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
