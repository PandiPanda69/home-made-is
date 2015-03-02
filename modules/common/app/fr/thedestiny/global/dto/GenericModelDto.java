package fr.thedestiny.global.dto;

import java.io.IOException;

import play.libs.Json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.global.model.Model;

/**
 * DTO générique pour l'ensemble des modèles
 * @author Sébastien
 * @param <T> Le type du modèle à mapper
 */
@Deprecated
public class GenericModelDto<T> extends AbstractDto {

	protected T model;

	/**
	 * Constructeur
	 * @param model Modèle
	 */
	public GenericModelDto(T model) {
		this.model = model;
	}

	/**
	 * Constructeur
	 * @param json Noeud JSON
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public GenericModelDto(JsonNode json, Class<? extends Model> modelClass) throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {

		ObjectMapper mapper = new ObjectMapper();
		model = (T) mapper.readValue(json.toString(), modelClass);
	}

	/**
	 * Map l'objet en noeud JSON
	 * @return Un noeud JSON
	 */
	@Override
	public JsonNode toJson() {
		return Json.toJson(model);
	}

	/**
	 * Retourne le modèle
	 * @return Le modèle instancié
	 */
	public T asObject() {
		return model;
	}
}
