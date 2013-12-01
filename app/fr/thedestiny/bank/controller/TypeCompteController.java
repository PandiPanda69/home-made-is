package fr.thedestiny.bank.controller;

import java.util.List;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.bank.dto.GenericModelDto;
import fr.thedestiny.bank.dto.TypeCompteDto;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.bank.service.TypeCompteService;
import fr.thedestiny.bank.service.exception.TypeCompteInUseException;
import fr.thedestiny.global.helper.ResultFactory;

public class TypeCompteController extends Controller {

	private static TypeCompteService typeService = TypeCompteService.getInstance();

	@Transactional(readOnly = false)
	@Security
	public static Result list() {

		List<TypeCompteDto> dto = typeService.findAllTypes();
		return ok(Json.toJson(dto));
	}

	@Transactional
	@Security
	public static Result add() {

		TypeCompteDto outDto = null;
		try {
			GenericModelDto<TypeCompte> dto = new GenericModelDto<TypeCompte>(ctx().request().body().asJson(), TypeCompte.class);
			outDto = typeService.saveTypeCompte(dto);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Transactional
	@Security
	public static Result edit(Integer typeId) {

		TypeCompteDto outDto = null;
		try {
			GenericModelDto<TypeCompte> dto = new GenericModelDto<TypeCompte>(ctx().request().body().asJson(), TypeCompte.class);
			outDto = typeService.saveTypeCompte(dto);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Transactional
	@Security
	public static Result delete(Integer typeId) {

		try {
			typeService.deleteTypeCompte(typeId);
		} catch (TypeCompteInUseException ex) {
			ObjectNode inUseNode = JsonNodeFactory.instance.objectNode();
			inUseNode.put("code", "fail");
			inUseNode.put("msg", "Type de compte actuellement utilisé. Sa suppression est impossible.");
			return badRequest(inUseNode);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
