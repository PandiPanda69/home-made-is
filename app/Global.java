import java.lang.reflect.Method;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import fr.thedestiny.auth.security.SecurityHelper;
import fr.thedestiny.global.config.SpringConfiguration;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		Logger.info("Reading spring context.");
		SpringConfiguration.appContext = new ClassPathXmlApplicationContext("spring-context.xml");
		Logger.info("Context has been read.");

	}

	@Override
	public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
		try {
			return SpringConfiguration.appContext.getBean(controllerClass);
		} catch (NoSuchBeanDefinitionException ex) {
			return null;
		}
	}

	@Override
	public Action<?> onRequest(final Http.Request request, final Method actionMethod) {
		return new ActionWrapper(super.onRequest(request, actionMethod));
	}

	private class ActionWrapper extends Action.Simple {
		public ActionWrapper(final Action<?> action) {
			this.delegate = action;
		}

		@Override
		public Promise<SimpleResult> call(final Http.Context ctx) throws Throwable {
			if (ctx.request().path().startsWith("/api")) {
				ctx.response().setHeader("Access-Control-Allow-Origin", "*");
				ctx.response().setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
				ctx.response().setHeader("Access-Control-Allow-Headers", "Content-type, " + SecurityHelper.HEADER_AUTH_TOKEN);
			}
			return this.delegate.call(ctx);
		}
	};
}
