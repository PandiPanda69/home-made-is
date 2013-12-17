package fr.thedestiny.global.helper;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Controller;
import play.mvc.Result;

public class ResultFactory {

	protected static ObjectNode okNode;
	protected static ObjectNode failNode;

	public static Result OK;
	public static Result FAIL;

	static {
		okNode = JsonNodeFactory.instance.objectNode();
		okNode.put("code", "ok");

		failNode = JsonNodeFactory.instance.objectNode();
		failNode.put("code", "fail");

		OK = Controller.ok(okNode);
		FAIL = Controller.internalServerError(failNode);
	}
}