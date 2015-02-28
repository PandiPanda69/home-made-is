package fr.thedestiny.global.dto;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;

public abstract class AbstractDto {

	public JsonNode toJson() {
		return Json.toJson(this);
	}
}
