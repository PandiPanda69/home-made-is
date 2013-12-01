package fr.thedestiny.bank.controller;

import java.util.List;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.CompteDto;
import fr.thedestiny.bank.dto.GenericModelDto;
import fr.thedestiny.bank.dto.StatsDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.service.CompteService;
import fr.thedestiny.global.helper.ResultFactory;

public class CompteController extends Controller {

	private static CompteService compteService = CompteService.getInstance();

	@Security
	@Transactional(readOnly = true)
	public static Result list() {

		UserDto currentUser = SecurityHelper.getLoggedUser();

		List<CompteDto> list = compteService.listCompteForUser(currentUser.getId());
		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public static Result add() {

		CompteDto outDto = null;
		try {
			GenericModelDto<Compte> dto = new GenericModelDto<Compte>(ctx().request().body().asJson(), Compte.class);
			outDto = compteService.saveCompte(dto, SecurityHelper.getLoggedUserId());
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Security
	@Transactional
	public static Result edit(Integer id) {

		CompteDto outDto = null;
		try {
			GenericModelDto<Compte> dto = new GenericModelDto<Compte>(ctx().request().body().asJson(), Compte.class);
			outDto = compteService.saveCompte(dto, SecurityHelper.getLoggedUserId());
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(outDto.toJson());
	}

	@Security
	@Transactional
	public static Result delete(Integer id) {

		try {
			compteService.deleteCompte(id);
		} catch (Exception ex) {
			Logger.error("Error while deleting", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}

	@Security
	@Transactional(readOnly = true)
	public static Result getAccountStatsPerMonth(Integer idAccount) {

		StatsDto statsDto = null;
		try {
			statsDto = compteService.getStatsPerMonthForAccount(SecurityHelper.getLoggedUserId(), idAccount);
		} catch (Exception ex) {
			Logger.error("Error while getting stats.", ex);
			return ResultFactory.FAIL;
		}

		return ok(statsDto.toJson());
	}
}
