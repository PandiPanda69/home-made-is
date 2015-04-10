package fr.thedestiny.torrent.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.torrent.dao.TorrentDirectoryDao;
import fr.thedestiny.torrent.dto.TorrentDto;
import fr.thedestiny.torrent.dto.TorrentFileDto;

@Service
public class FileService {

	@Autowired
	private TorrentDirectoryDao torrentDirectoryDao;

	public TorrentFileDto getTorrentFiles(final TorrentDto torrent) {

		String torrentPath =
				new StringBuilder(torrentDirectoryDao.getTargetDirectory())
		.append(File.separator)
		.append(torrent.getName())
		.toString();

		File file = new File(torrentPath);
		if (!file.exists()) {
			return null;
		}

		TorrentFileDto root = new TorrentFileDto(torrent.getName(), file.length());
		if (file.isFile()) {
			return root;
		}

		exploreDirectory(root, file);
		return root;
	}

	private void exploreDirectory(final TorrentFileDto dto, final File file) {

		List<TorrentFileDto> children = new ArrayList<>();
		for (final File current : file.listFiles()) {

			TorrentFileDto currentDto = new TorrentFileDto(current.getName(), current.length());

			if (current.isDirectory()) {
				exploreDirectory(currentDto, current);
			}

			children.add(currentDto);
		}

		dto.setChildren(children);
	}
}
