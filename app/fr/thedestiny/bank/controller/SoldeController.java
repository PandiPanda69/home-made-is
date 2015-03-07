package fr.thedestiny.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.SoldeDto;
import fr.thedestiny.bank.service.SoldeService;

@org.springframework.stereotype.Controller
public class SoldeController extends Controller {

	@Autowired
	private SoldeService soldeService;

	@Security
	@Transactional
	public Result get(final Integer idAccount, final Integer idMois) {

		SoldeDto solde = soldeService.getBalanceAtBeginningOfMonth(SecurityHelper.getLoggedUserId(), idAccount, idMois);
		return ok(solde.toJson());
	}
}
