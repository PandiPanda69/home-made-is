package fr.thedestiny.bank.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.bank.dto.TypeCompteDto;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.bank.service.TypeCompteService;
import fr.thedestiny.bank.service.exception.TypeCompteInUseException;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class TypeCompteController extends Controller {

	@Autowired
	private TypeCompteService typeService;

	@Autowired
	private ObjectMapper mapper;

	@Transactional(readOnly = false)
	@Security
	public Result list() {
		List<TypeCompteDto> dto = typeService.findAllTypes();
		return ok(Json.toJson(dto));
	}

	@Transactional
	@Security
	public Result add() {

		TypeCompteDto outDto = null;
		try {
			TypeCompte type = mapper.readValue(ctx().request().body().asJson().toString(), TypeCompte.class);
			outDto = typeService.saveTypeCompte(type);
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Transactional
	@Security
	public Result edit(final Integer typeId) {

		TypeCompteDto outDto = null;
		try {
			TypeCompte type = mapper.readValue(ctx().request().body().asJson().toString(), TypeCompte.class);
			outDto = typeService.saveTypeCompte(type);
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Transactional
	@Security
	public Result delete(final Integer typeId) {

		try {
			typeService.deleteTypeCompte(typeId);
		} catch (Exception ex) {
			ObjectNode inUseNode = JsonNodeFactory.instance.objectNode();
			inUseNode.put("code", "fail");
			inUseNode.put("msg", "Type de compte actuellement utilisé. Sa suppression est impossible.");
			return badRequest(inUseNode);
		}

		return ResultFactory.OK;
	}
}
