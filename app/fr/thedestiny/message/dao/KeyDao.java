package fr.thedestiny.message.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.ConfigurationException;

import org.springframework.stereotype.Repository;

import play.Logger;
import play.Play;

// TODO : Add salt.
@Repository
public class KeyDao {

	private static final String KEY_REPOSITORY_PROPERTY = "message.key_repository";

	private static final String keyRepository = Play.application().configuration().getString(KEY_REPOSITORY_PROPERTY);

	private static long cacheLastUpdate = 0L;
	private static List<String> cacheContent = null;

	private static Lock lock = new ReentrantLock();

	public List<String> getAllKeys() throws IOException, ConfigurationException {

		if (keyRepository == null) {
			throw new ConfigurationException(KEY_REPOSITORY_PROPERTY);
		}

		File repositoryFile = new File(keyRepository);
		if (!repositoryFile.exists()) {
			throw new FileNotFoundException(keyRepository);
		}

		long repositoryLastUpdate = repositoryFile.lastModified();

		lock.lock();
		if (repositoryLastUpdate > cacheLastUpdate) {
			Logger.info("Refreshing message key repository...");
			cacheContent = readKeyRepository(repositoryFile);
			cacheLastUpdate = repositoryLastUpdate;
		}
		lock.unlock();

		return cacheContent;
	}

	private List<String> readKeyRepository(final File repositoryFile) throws IOException {

		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(repositoryFile));

			String line;
			do {
				line = reader.readLine();
				if (line != null && !line.isEmpty()) {
					result.add(line);
				}
			} while (line != null);

		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return result;
	}
}
