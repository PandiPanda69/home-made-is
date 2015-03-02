package fr.thedestiny.torrent.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.global.helper.DataUnitHelper;
import fr.thedestiny.torrent.dao.TorrentDirectoryDao;

@Service
public class ParametersService {

	@Autowired
	private TorrentDirectoryDao directoryDao;

	protected ParametersService() {
	}

	public Map<String, String> getParameters() {
		Map<String, String> parameters = new TreeMap<>();

		parameters.put("target", directoryDao.getTargetDirectory());
		parameters.put("repository", directoryDao.getRepositoryDirectory());

		String minSpace = null;
		Long minimumSpaceLimit = directoryDao.getMinimumSpaceLimit();
		if (minimumSpaceLimit != null) {
			minSpace = DataUnitHelper.fit(minimumSpaceLimit).toString();
		}
		else {
			minSpace = "N/A";
		}

		parameters.put("minSpace", minSpace);

		return parameters;
	}
}
