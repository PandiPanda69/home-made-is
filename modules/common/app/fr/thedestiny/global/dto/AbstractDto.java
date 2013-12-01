package fr.thedestiny.global.dto;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;

public abstract class AbstractDto {

	public JsonNode toJson() {
		return Json.toJson(this);
	}
}
