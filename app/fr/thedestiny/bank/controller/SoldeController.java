package fr.thedestiny.bank.controller;

import play.Logger;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.SoldeDto;
import fr.thedestiny.bank.service.SoldeService;

public class SoldeController extends Controller {

	private static SoldeService soldeService = SoldeService.getInstance();

	@Security
	@Transactional
	public static Result get(Integer idAccount, Integer idMois) {

		SoldeDto solde = null;
		try {
			solde = soldeService.getBalanceAtBeginningOfMonth(SecurityHelper.getLoggedUserId(), idAccount, idMois);
		} catch (Exception ex) {
			Logger.error("SoldeController", ex);
			return badRequest();
		}

		return ok(solde.toJson());
	}
}
