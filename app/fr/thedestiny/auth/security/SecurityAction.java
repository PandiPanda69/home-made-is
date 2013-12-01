package fr.thedestiny.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import fr.thedestiny.auth.controller.AuthenticationController;

@Component
public class SecurityAction extends Action<Security> {

	@Autowired
	private AuthenticationController authenticationController;

	@Override
	public Result call(Context ctx) throws Throwable {

		if (configuration.logged() && (!SecurityHelper.isLoggedOn() || SecurityHelper.getLoggedUser() == null)) {
			return authenticationController.index();
		}

		if (configuration.restrictedAccess() && !SecurityHelper.isAdmin()) {
			return authenticationController.notGranted();
		}

		return delegate.call(ctx);
	}

}
