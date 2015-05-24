package fr.thedestiny.bank.controller;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dto.OperationDto;
import fr.thedestiny.bank.dto.SearchResultDto;
import fr.thedestiny.bank.service.OperationService;
import fr.thedestiny.bank.service.SoldeService;
import fr.thedestiny.global.helper.ResultFactory;

@org.springframework.stereotype.Controller
public class OperationController extends Controller {

	@Autowired
	private OperationService operationService;

	@Autowired
	private SoldeService soldeService;

	@Autowired
	private ObjectMapper mapper;

	@Security
	@Transactional(readOnly = true)
	public Result list(final Integer idAccount, final Integer idMonth) {

		List<OperationDto> list = operationService.findAllOperationsForMonth(SecurityHelper.getLoggedUserId(), idAccount, idMonth);
		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public Result add(final Integer idAccount, final Integer idMonth) {

		OperationDto dto = null;
		try {
			Integer userId = SecurityHelper.getLoggedUserId();

			dto = mapper.readValue(ctx().request().body().asJson().toString(), OperationDto.class);
			dto = operationService.addOperation(dto, userId, idAccount, idMonth);

			// Update month solds (chained)
			soldeService.updateSolde(userId, idAccount, idMonth);

		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public Result edit(final Integer idAccount, final Integer idMonth, final Integer id) {

		OperationDto dto = null;
		try {
			dto = mapper.readValue(ctx().request().body().asJson().toString(), OperationDto.class);

			Integer userId = SecurityHelper.getLoggedUserId();
			dto = operationService.updateOperation(dto, userId, idAccount, idMonth);

			// Update month solds (chained)
			soldeService.updateSolde(userId, idAccount, idMonth);

		} catch (IOException ex) {
			Logger.error("Error while unserializing", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public Result delete(final Integer idAccount, final Integer idMois, final Integer id) {

		Integer userId = SecurityHelper.getLoggedUserId();
		if (!operationService.deleteOperation(userId, idAccount, idMois, id)) {
			return ResultFactory.FAIL;
		}

		// Update month solds (chained)
		soldeService.updateSolde(userId, idAccount, idMois);

		return ResultFactory.OK;
	}

	@Security
	public Result currentYearOp(final Integer accountId) {

		List<OperationDto> dto = operationService.getCurrentYearOperation(accountId);
		return ok(Json.toJson(dto));
	}

	@Security
	public Result search(final String value) {

		int userId = SecurityHelper.getLoggedUserId();

		try {
			List<SearchResultDto> dto = operationService.findOperations(value, userId);
			int resultCount = dto.size();
			String json = mapper.writeValueAsString(dto);

			return ok(fr.thedestiny.bank.views.html.searchresult.render(json, resultCount));
		} catch (SolrServerException | IOException ex) {
			return internalServerError(ex.getMessage());
		}
	}
}
