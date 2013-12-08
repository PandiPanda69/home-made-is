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
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.service.MoisAnneeService;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.helper.ResultFactory;

public class MoisAnneeController extends Controller {

	private static MoisAnneeService monthService = MoisAnneeService.getInstance();

	@Security
	@Transactional(readOnly = true)
	public static Result list(Integer compteId) {

		UserDto currentUser = SecurityHelper.getLoggedUser();

		List<MoisAnnee> list = null;
		try {
			list = monthService.findAccountMonths(currentUser.getId(), compteId);
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public static Result add(Integer idAccount) {

		GenericModelDto<MoisAnnee> mois = null;
		try {
			mois = new GenericModelDto<MoisAnnee>(ctx().request().body().asJson(), MoisAnnee.class);
			mois = monthService.addMonth(mois, idAccount, SecurityHelper.getLoggedUserId());
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(mois.asJson());
	}
}
