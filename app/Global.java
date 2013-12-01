import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;
import play.GlobalSettings;
import play.Logger;
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
}
