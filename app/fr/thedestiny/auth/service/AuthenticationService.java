package fr.thedestiny.auth.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.auth.dao.UtilisateurDao;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class AuthenticationService extends AbstractService {

	@Autowired
	private UtilisateurDao utilisateurDao;

	public Utilisateur authenticate(final String username, final String password) {
		List<Utilisateur> users = processInTransaction(new InTransactionFunction() {

			@SuppressWarnings("unchecked")
			@Override
			public List<Utilisateur> doWork(EntityManager em) {
				return utilisateurDao.findByUsername(em, username);
			}
		});

		// Encode password in SHA-1
		String encodedPassword = encodePassword(password);

		if (users.size() == 1 && encodedPassword.equals(users.get(0).getPassword())) {
			return users.get(0);
		}

		return null;
	}

	public String encodePassword(final String password) {
		String encodedPassword = new String();

		try {
			final byte[] buffer = MessageDigest.getInstance("SHA-1").digest(password.getBytes());
			for (byte b : buffer) {
				encodedPassword += String.format("%02x", b);
			}
		} catch (NoSuchAlgorithmException ex) {
			Logger.error(ex.getMessage());
		}

		return encodedPassword;
	}
}
