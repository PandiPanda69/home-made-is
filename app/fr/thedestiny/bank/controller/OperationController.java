package fr.thedestiny.bank.controller;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.bank.dao.HeuristiqueTypeDao;
import fr.thedestiny.bank.dto.OperationDto;
import fr.thedestiny.bank.models.HeuristiqueType;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.service.OperationService;
import fr.thedestiny.bank.service.SoldeService;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.helper.ResultFactory;

public class OperationController extends Controller {

	private static OperationService operationService = OperationService.getInstance();
	private static SoldeService soldeService = SoldeService.getInstance();

	@Security
	@Transactional(readOnly = true)
	public static Result list(Integer idAccount, Integer idMonth) {

		List<OperationDto> list = operationService.findAllOperationsForMonth(SecurityHelper.getLoggedUserId(), idAccount, idMonth);
		return ok(Json.toJson(list));
	}

	@Security
	@Transactional
	public static Result add(Integer idAccount, Integer idMonth) throws Exception {

		OperationDto dto = null;
		try {
			Integer userId = SecurityHelper.getLoggedUserId();

			dto = new ObjectMapper().readValue(ctx().request().body().asJson(), OperationDto.class);
			dto = operationService.addOperation(dto, userId, idAccount, idMonth);

			// Update month solds (chained)
			soldeService.updateSolde(userId, idAccount, idMonth);

		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public static Result edit(Integer idAccount, Integer idMonth, Integer id) {

		OperationDto dto = null;
		try {
			dto = new ObjectMapper().readValue(ctx().request().body().asJson(), OperationDto.class);

			Integer userId = SecurityHelper.getLoggedUserId();

			dto = operationService.updateOperation(dto, userId, idAccount, idMonth);

			// Update month solds (chained)
			soldeService.updateSolde(userId, idAccount, idMonth);

		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ok(dto.toJson());
	}

	@Security
	@Transactional
	public static Result delete(Integer idAccount, Integer idMois, Integer id) {

		try {
			Integer userId = SecurityHelper.getLoggedUserId();
			operationService.deleteOperation(userId, idAccount, idMois, id);

			// Update month solds (chained)
			soldeService.updateSolde(userId, idAccount, idMois);
		} catch (Exception ex) {
			Logger.error("Error while saving", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}

	@Security
	@Transactional
	public static Result importData(Integer idAccount, Integer idMois) {

		JsonNode body = ctx().request().body().asJson();

		GenericModelDto<Operation> dto = null;
		JsonNode current = null;
		for (int i = 0; i < body.size(); i++) {

			try {
				current = body.get(i);
				dto = new GenericModelDto<Operation>(current, Operation.class);

				// FIXME Nettoyer cette caca
				Operation model = dto.asObject();
				HeuristiqueType type = HeuristiqueTypeDao.findByNom(model.getNom());
				if (type != null) {
					model.setType(type.getType());
					dto = new GenericModelDto<Operation>(model);
				}

				//				OperationDelegate.add(dto, idAccount, idMois);

			} catch (Exception ex) {
				Logger.error("Error while importing data", ex);
				return ResultFactory.FAIL;
			}
		}

		return ResultFactory.OK;
	}
}
