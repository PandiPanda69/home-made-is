package fr.thedestiny.bank.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.MotifOperationDto;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.bank.service.MotifOperationService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class MotifOperationController extends Controller {

	@Autowired
	private MotifOperationService motifService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	@Transactional(readOnly = true)
	public Result list() {

		Integer userId = SecurityHelper.getLoggedUserId();
		return ok(Json.toJson(motifService.findAllMotifs(userId)));
	}

	@Security
	@Transactional
	public Result add() {

		MotifOperationDto dto = null;
		try {
			MotifOperation motif = mapper.readValue(ctx().request().body().asJson().toString(), MotifOperation.class);
			dto = motifService.addMotif(motif, SecurityHelper.getLoggedUserId());
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public Result delete(final Integer motifId) {

		Integer userId = SecurityHelper.getLoggedUserId();
		if (!motifService.deleteMotif(userId, motifId)) {
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}
}
