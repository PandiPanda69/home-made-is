package fr.thedestiny.bank.controller;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.MotifOperationDto;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.bank.service.MotifOperationService;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.helper.ResultFactory;

public class MotifOperationController extends Controller {

	private static MotifOperationService motifService = MotifOperationService.getInstance();

	@Security
	@Transactional(readOnly = true)
	public static Result list() {

		Integer userId = SecurityHelper.getLoggedUserId();
		return ok(Json.toJson(motifService.findAllMotifs(userId)));
	}

	@Security
	@Transactional
	public static Result add() throws Exception {

		GenericModelDto<MotifOperation> motif = new GenericModelDto<MotifOperation>(ctx().request().body().asJson(), MotifOperation.class);
		MotifOperationDto dto = motifService.addMotif(motif, SecurityHelper.getLoggedUserId());

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public static Result delete(Integer motifId) throws Exception {

		Integer userId = SecurityHelper.getLoggedUserId();
		motifService.deleteMotif(userId, motifId);
		return ResultFactory.OK;
	}
}
