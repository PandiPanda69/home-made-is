package fr.thedestiny.auth.service;

import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.auth.dao.UtilisateurDao;
import fr.thedestiny.auth.model.Utilisateur;

@Service
public class AuthenticationService {

	@Autowired
	private UtilisateurDao utilisateurDao;

	public Utilisateur authenticate(String username, String password) {
		List<Utilisateur> users = utilisateurDao.findByUsername(username);

		// Encode password in SHA-1
		String encodedPassword = encodePassword(password);

		if (users.size() == 1 && encodedPassword.equals(users.get(0).getPassword())) {
			return users.get(0);
		}

		return null;
	}

	public String encodePassword(String password) {
		String encodedPassword = new String();

		try {
			byte[] buffer = MessageDigest.getInstance("SHA-1").digest(password.getBytes());
			for (byte b : buffer) {
				encodedPassword += String.format("%02x", b);
			}
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
		}

		return encodedPassword;
	}
}
