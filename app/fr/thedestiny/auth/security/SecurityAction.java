package fr.thedestiny.auth.security;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import play.libs.F.Function0;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;
import fr.thedestiny.auth.controller.AuthenticationController;

@Component
public class SecurityAction extends Action<Security> {

	@Autowired
	private AuthenticationController authenticationController;

	@Override
	public Promise<SimpleResult> call(final Context ctx) throws Throwable {

		return Promise.promise(new Function0<SimpleResult>() {

			@Override
			public SimpleResult apply() throws Throwable {

				if (configuration.logged() && (!SecurityHelper.isLoggedOn() || SecurityHelper.getLoggedUser() == null)) {
					return (SimpleResult) authenticationController.index();
				}
				else if (configuration.restrictedAccess() && !SecurityHelper.isAdmin()) {
					return (SimpleResult) authenticationController.notGranted();
				}

				return delegate.call(ctx).get(24, TimeUnit.HOURS);
			}

		});
	}
}
