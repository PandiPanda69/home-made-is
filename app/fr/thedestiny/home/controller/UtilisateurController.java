package fr.thedestiny.home.controller;

import java.io.IOException;
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

	@Autowired
	private ObjectMapper mapper;

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
				Logger.error("Adding user with specified id.");
				return ResultFactory.FAIL;
			}

			dto = userService.saveNewUser(dto);

		} catch (IOException ex) {
			Logger.error("Error while serializing JSON", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result edit(final int id) {

		UserDto dto = null;
		try {
			dto = mapper.readValue(ctx().request().body().asJson().toString(), UserDto.class);

			if (dto.getId() == null) {
				Logger.error("Empty id submitted.");
				return ResultFactory.FAIL;
			}

			dto = userService.saveUser(dto);
		} catch (IOException ex) {
			Logger.error("Error while serializing JSON", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Transactional
	@Security(restrictedAccess = true)
	public Result delete(Integer id) {

		if (!userService.deleteUser(id)) {
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
