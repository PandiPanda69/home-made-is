package fr.thedestiny.auth.security;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;
import fr.thedestiny.auth.controller.AuthenticationController;

@Component
public class SecurityAction extends Action<Security> {

	private static enum UserState {

		NOT_LOGGED,
		NOT_GRANTED,
		OK;
	};

	@Autowired
	private AuthenticationController authenticationController;

	@Override
	public Promise<SimpleResult> call(final Context ctx) throws Throwable {

		return Akka.future(new Callable<UserState>() {

			@Override
			public UserState call() throws Exception {

				if (configuration.logged() && (!SecurityHelper.isLoggedOn() || SecurityHelper.getLoggedUser() == null)) {
					return UserState.NOT_LOGGED;
				}
				else if (configuration.restrictedAccess() && !SecurityHelper.isAdmin()) {
					return UserState.NOT_GRANTED;
				}

				return UserState.OK;
			}
		})
				.map(new Function<SecurityAction.UserState, SimpleResult>() {

					@Override
					public SimpleResult apply(UserState state) throws Throwable {

						switch (state) {
						case NOT_LOGGED:
							return (SimpleResult) authenticationController.index();
						case NOT_GRANTED:
							return (SimpleResult) authenticationController.notGranted();

						default:
							return delegate.call(ctx).get(24, TimeUnit.HOURS);
						}
					}
				});
	}
}
