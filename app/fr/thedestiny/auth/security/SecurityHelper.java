package fr.thedestiny.auth.security;

import play.mvc.Controller;
import play.mvc.Http.Context;
import fr.thedestiny.auth.dto.UserDto;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.auth.service.UserService;

/**
 * 
 * @author Sébastien
 */
public class SecurityHelper {

	private final static String KEY_USER_ID = "user_id";

	private static UserService userService = UserService.getInstance();

	public static void setUserSession(Utilisateur user) {

		Context ctx = Controller.ctx();

		if (user != null) {
			ctx.session().put(KEY_USER_ID, user.getId().toString());
		} else {
			ctx.session().remove(KEY_USER_ID);
		}
	}

	public static boolean isLoggedOn() {
		String userId = Controller.ctx().session().get(KEY_USER_ID);

		return !(userId == null || userId.isEmpty());
	}

	public static boolean isAdmin() {
		return getLoggedUser().isAdmin();
	}

	public static UserDto getLoggedUser() {
		return userService.findUserById(getLoggedUserId());
	}

	public static Integer getLoggedUserId() {
		String userId = Controller.ctx().session().get(KEY_USER_ID);

		if (userId == null) {
			return null;
		}

		return Integer.valueOf(userId);
	}

	//	public static boolean doesUserOwnAccount(Integer accountId) {
	//
	//		Integer userId = getLoggedUserId();
	//		Compte compte = CompteDao.findById(accountId);
	//
	//		return compte != null && compte.getOwner().getId().equals(userId);
	//	}
}
