package fr.thedestiny.message.service;

import java.io.IOException;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.message.dao.KeyDao;

@Service
public class KeyVerifierService {

	@Autowired
	private KeyDao keyDao;

	/**
	 * Check whether key is well registered.
	 * @param key Key to verify
	 * @return True if the key is valid, false otherwise.
	 */
	public boolean isKeyValid(final String key) {

		if (key == null || key.isEmpty()) {
			return false;
		}

		try {
			return keyDao.getAllKeys().contains(key);
		} catch (IOException ex) {
			Logger.error("Error while reading keys.", ex);
			return false;
		} catch (ConfigurationException ex) {
			Logger.error("Wrong configuration.", ex);
			return false;
		}
	}
}
