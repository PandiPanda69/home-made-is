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
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.service.MoisAnneeService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class MoisAnneeController extends Controller {

	@Autowired
	private MoisAnneeService monthService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	@Transactional(readOnly = true)
	public Result list(final Integer compteId) {

		UserDto currentUser = SecurityHelper.getLoggedUser();

		List<MoisAnnee> list = null;
		try {
            Logger.info("Find months for account {}", compteId);
			list = monthService.findAccountMonths(currentUser.getId(), compteId);
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public Result add(final Integer idAccount) {

		MoisAnnee mois = null;
		try {
			mois = mapper.readValue(ctx().request().body().asJson().toString(), MoisAnnee.class);
			if (mois == null) {
				return ResultFactory.FAIL;
			}

			mois = monthService.addMonth(mois, idAccount, SecurityHelper.getLoggedUserId());
		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(Json.toJson(mois));
	}
}
