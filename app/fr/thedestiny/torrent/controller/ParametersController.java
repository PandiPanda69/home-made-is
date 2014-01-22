package fr.thedestiny.torrent.controller;

import org.springframework.beans.factory.annotation.Autowired;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.torrent.service.ParametersService;

@org.springframework.stereotype.Controller
public class ParametersController extends Controller {

	@Autowired
	private ParametersService parametersService;

	public Result home() {
		return ok(Json.toJson(parametersService.getParameters()));
	}
}
