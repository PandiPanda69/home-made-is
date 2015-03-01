package fr.thedestiny.torrent.dao;

import java.io.File;

import org.springframework.stereotype.Repository;

import play.Play;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.DataUnitHelper;

@Repository
public class TorrentDirectoryDao {

	private static final String PROPERTY_TARGET = "torrent.target";
	private static final String PROPERTY_REPOSITORY = "torrent.repository";
	private static final String PROPERTY_MIN_SPACE = "torrent.min_space";

	public TorrentDirectoryDao() {
	}

	public DataUnit getFreeSpaceInTargetDirectory() {

		String torrentDirectory = getTargetDirectory();
		File targetDirectory = new File(torrentDirectory);

		return DataUnitHelper.fit(targetDirectory.getFreeSpace());
	}

	public String getTargetDirectory() {
		return Play.application().configuration().getString(PROPERTY_TARGET);
	}

	public String getRepositoryDirectory() {
		return Play.application().configuration().getString(PROPERTY_REPOSITORY);
	}

	public Long getMinimumSpaceLimit() {
		return Play.application().configuration().getLong(PROPERTY_MIN_SPACE);
	}
}
