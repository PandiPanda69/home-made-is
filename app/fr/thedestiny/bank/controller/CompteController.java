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

import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.CompteDto;
import fr.thedestiny.bank.dto.StatsDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.service.CompteService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class CompteController extends Controller {

	@Autowired
	private CompteService compteService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	@Transactional(readOnly = true)
	public Result list() {

		UserDto currentUser = SecurityHelper.getLoggedUser();

		List<CompteDto> list = compteService.listCompteForUser(currentUser.getId());
		return ok(Json.toJson(list));
	}

	@Transactional
	public Result add() {

		CompteDto outDto = null;
		try {
			Compte compte = mapper.readValue(ctx().request().body().asJson().toString(), Compte.class);
			outDto = compteService.saveCompte(compte, SecurityHelper.getLoggedUserId());
		} catch (IOException ex) {
			Logger.error("Error while serializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Security
	@Transactional
	public Result edit(final Integer id) {

		CompteDto outDto = null;
		try {
			Compte compte = mapper.readValue(ctx().request().body().asJson().toString(), Compte.class);
			outDto = compteService.saveCompte(compte, SecurityHelper.getLoggedUserId());
		} catch (IOException ex) {
			Logger.error("Error while serializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Security
	@Transactional
	public Result delete(final Integer id) {

		if (!compteService.deleteCompte(id)) {
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}

	@Security
	@Transactional(readOnly = true)
	public Result getAccountStatsPerMonth(final Integer idAccount) {

		StatsDto statsDto = compteService.getStatsPerMonthForAccount(SecurityHelper.getLoggedUserId(), idAccount);
		return ok(statsDto.toJson());
	}
}
