package fr.thedestiny.bank.controller;

import java.util.List;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.bank.service.OperationTypeService;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.helper.ResultFactory;

public class OperationTypeController extends Controller {

	private static OperationTypeService operationTypeService = OperationTypeService.getInstance();

	@Security
	@Transactional(readOnly = true)
	public static Result list() {

		List<OperationType> list = operationTypeService.findAllOperationTypes();
		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public static Result add() {

		GenericModelDto<OperationType> optype = null;
		try {
			optype = new GenericModelDto<OperationType>(ctx().request().body().asJson(), OperationType.class);
			optype = operationTypeService.addOperationType(optype);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(optype.toJson());
	}

	@Security
	@Transactional
	public static Result edit(Integer id) {

		GenericModelDto<OperationType> optype = null;
		try {
			optype = new GenericModelDto<OperationType>(ctx().request().body().asJson(), OperationType.class);
			optype = operationTypeService.updateOperationType(optype);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(optype.toJson());
	}

	@Security
	@Transactional
	public static Result delete(Integer id) {

		try {
			operationTypeService.deleteOperationType(id);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
