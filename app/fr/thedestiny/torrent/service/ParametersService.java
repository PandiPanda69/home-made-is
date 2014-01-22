package fr.thedestiny.torrent.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.torrent.dao.TorrentDirectoryDao;

@Service
public class ParametersService {

	@Autowired
	private TorrentDirectoryDao directoryDao;

	public ParametersService() {
	}

	public Map<String, String> getParameters() {
		Map<String, String> parameters = new TreeMap<String, String>();

		parameters.put("target", directoryDao.getTargetDirectory());
		parameters.put("repository", directoryDao.getRepositoryDirectory());

		String minSpace = null;
		Long minimumSpaceLimit = directoryDao.getMinimumSpaceLimit();
		if (minimumSpaceLimit != null) {
			DataUnit minimumSpaceUnit = new DataUnit(minimumSpaceLimit);
			minSpace = minimumSpaceUnit.getValue() + " " + minimumSpaceUnit.getUnit();
		}
		else {
			// TODO : Internationalisation
			minSpace = "Non défini";
		}

		parameters.put("minSpace", minSpace);

		return parameters;
	}
}
