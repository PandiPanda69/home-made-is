package fr.thedestiny.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.RepetitionDto;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.service.OperationService;
import fr.thedestiny.bank.service.RepetitionService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class RepetitionController extends Controller {

	@Autowired
	private OperationService operationService;

	@Autowired
	private RepetitionService repetitionService;

	@Security
	@Transactional
	public Result add(Integer op) {

		Operation operation = operationService.findOperationById(op, SecurityHelper.getLoggedUserId());
		repetitionService.save(operation);

		return ResultFactory.OK;
	}

	@Security
	@Transactional
	public Result find(Integer account) {

		List<RepetitionDto> dto = repetitionService.findByAccount(account, SecurityHelper.getLoggedUserId());
		return ok(Json.toJson(dto));
	}

	@Security
	@Transactional
	public Result delete(Integer id, Integer account) {

		if (repetitionService.deleteRepetition(account, id, SecurityHelper.getLoggedUserId())) {
			return ResultFactory.OK;
		}

		return ResultFactory.FAIL;
	}
}
