package fr.thedestiny.torrent.dao;

import java.io.File;

import org.springframework.stereotype.Repository;

import play.Play;
import fr.thedestiny.global.util.DataUnit;

@Repository
public class TorrentDirectoryDao {

	public TorrentDirectoryDao() {
	}

	public DataUnit getFreeSpaceInTargetDirectory() {

		String torrentDirectory = Play.application().configuration().getString("torrent.target");
		File targetDirectory = new File(torrentDirectory);

		return new DataUnit(targetDirectory.getFreeSpace());
	}
}
