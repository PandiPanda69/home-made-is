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

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.bank.service.OperationTypeService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class OperationTypeController extends Controller {

	@Autowired
	private OperationTypeService operationTypeService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	@Transactional(readOnly = true)
	public Result list() {

		List<OperationType> list = operationTypeService.findAllOperationTypes();
		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public Result add() {

		OperationType optype = null;
		try {
			optype = mapper.readValue(ctx().request().body().asJson().toString(), OperationType.class);
			optype = operationTypeService.addOperationType(optype);
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(optype));
	}

	@Security
	@Transactional
	public Result edit(final Integer id) {

		OperationType optype = null;
		try {
			optype = mapper.readValue(ctx().request().body().asJson().toString(), OperationType.class);
			optype = operationTypeService.updateOperationType(optype);
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(optype));
	}

	@Security
	@Transactional
	public Result delete(final Integer id) {

		if (!operationTypeService.deleteOperationType(id)) {
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
