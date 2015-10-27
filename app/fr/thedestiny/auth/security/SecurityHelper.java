package fr.thedestiny.auth.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Context;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.auth.service.UserService;
import fr.thedestiny.global.config.SpringConfiguration;

/**
 *
 * @author Sébastien
 */
public class SecurityHelper {

	private final static String KEY_USER_ID = "user_id";
	public final static String HEADER_AUTH_TOKEN = "X-Auth-Token";

	private static UserService userService = SpringConfiguration.appContext.getBean(UserService.class);

	private static Map<String, Utilisateur> apiTokens = new HashMap<>();

	private SecurityHelper() {
	}

	public static void setUserSession(Utilisateur user) {

		Context ctx = Controller.ctx();

		if (user != null) {
			ctx.session().put(KEY_USER_ID, user.getId().toString());
		} else {
			ctx.session().remove(KEY_USER_ID);
		}
	}

	public static boolean isLoggedOn() {
		return getLoggedUserId() != null;
	}

	public static boolean isAdmin() {
		return getLoggedUser().isAdmin();
	}

	public static UserDto getLoggedUser() {
		return userService.findUserById(getLoggedUserId());
	}

	public static Integer getLoggedUserId() {
		Integer userId;

		String[] tokens = Controller.ctx().request().headers().get(HEADER_AUTH_TOKEN);
		if (tokens != null && tokens.length > 0 && !tokens[0].isEmpty()) {
			Utilisateur user;
			synchronized (apiTokens) {
				user = apiTokens.get(tokens[0]);
			}

			userId = user != null ? user.getId() : null;
		} else {
			String session = Controller.ctx().session().get(KEY_USER_ID);
			userId = session != null ? Integer.valueOf(session) : null;
		}

		return userId;
	}

	public static boolean isApiTokenPresent() {
		String[] tokens = Controller.ctx().request().headers().get(HEADER_AUTH_TOKEN);
		return (tokens != null && tokens.length > 0 && !tokens[0].isEmpty());
	}

	public static String createUserToken(final Utilisateur user) {
		String token = generateToken();
		synchronized (apiTokens) {
			while (apiTokens.containsKey(token)) {
				token = generateToken();
			}

			apiTokens.put(token, user);
		}

		return token;
	}

	public static void removeUserToken() {
		String[] tokens = Controller.ctx().request().headers().get(HEADER_AUTH_TOKEN);
		if (tokens != null && tokens.length > 0 && !tokens[0].isEmpty()) {
			synchronized (apiTokens) {
				apiTokens.remove(tokens[0]);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid token");
		}
	}

	private static String generateToken() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);

		StringBuffer encodedPassword = new StringBuffer();

		try {
			final byte[] buffer = MessageDigest.getInstance("SHA-1").digest(bytes);
			for (byte b : buffer) {
				encodedPassword.append(String.format("%02x", b));
			}
		} catch (NoSuchAlgorithmException ex) {
			Logger.error("Cannot encode password.", ex);
		}

		return encodedPassword.toString();
	}
}
