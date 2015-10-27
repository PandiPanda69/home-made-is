package fr.thedestiny.auth.security;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;
import fr.thedestiny.auth.controller.AuthenticationController;

@Component
public class SecurityAction extends Action<Security> {

	private static final Map<String, String> BAD_CREDENTIALS = new HashMap<>();

	@Autowired
	private AuthenticationController authenticationController;

	private SecurityAction() {
		BAD_CREDENTIALS.put("code", "fail");
		BAD_CREDENTIALS.put("msg", "Invalid token.");
	}

	@Override
	public Promise<SimpleResult> call(final Context ctx) throws Throwable {

		return Promise.promise(new Function0<SimpleResult>() {

			@Override
			public SimpleResult apply() throws Throwable {

				if (configuration.logged() && (!SecurityHelper.isLoggedOn() || SecurityHelper.getLoggedUser() == null)) {
					if (ctx.request().path().startsWith("/api") || SecurityHelper.isApiTokenPresent()) {
						ctx.response().setHeader(Http.Response.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
						return unauthorized(Json.toJson(BAD_CREDENTIALS));
					}
					else {
						return (SimpleResult) authenticationController.index();
					}
				}
				else if (configuration.restrictedAccess() && !SecurityHelper.isAdmin()) {
					return (SimpleResult) authenticationController.notGranted();
				}

				return delegate.call(ctx).get(24, TimeUnit.HOURS);
			}

		});
	}
}