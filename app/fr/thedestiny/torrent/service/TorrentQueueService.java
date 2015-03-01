package fr.thedestiny.torrent.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bencod.io.BencodFileInputStream;
import fr.thedestiny.bencod.parser.BencodParser;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.DataUnitHelper;
import fr.thedestiny.torrent.dao.TorrentDirectoryDao;
import fr.thedestiny.torrent.dto.TorrentQueueDto;

@Service
public class TorrentQueueService {

	@Autowired
	private TorrentDirectoryDao directoryDao;

	@SuppressWarnings("unchecked")
	public List<TorrentQueueDto> findQueuedTorrents() throws FileNotFoundException {

		File repository = new File(directoryDao.getRepositoryDirectory());
		if (!repository.exists() || !repository.isDirectory()) {
			throw new FileNotFoundException(repository.getName() + " does not refer a valid directory.");
		}

		File[] torrents = repository.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File current, String name) {
				return name.matches(".*torrent");
			}
		});

		List<TorrentQueueDto> result = new ArrayList<TorrentQueueDto>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (File current : torrents) {
			try {
				BencodFileInputStream bfis = new BencodFileInputStream(current);
				BencodParser parser = new BencodParser(bfis);

				Map<String, Object> torrentContent = (Map<String, Object>) parser.parse();

				long size = 0;
				Map<String, Object> info = (Map<String, Object>) torrentContent.get("info");
				List<Map<String, Object>> files = (List<Map<String, Object>>) info.get("files");
				for (Map<String, Object> torrentFile : files) {
					size += (Integer) torrentFile.get("length");
				}

				String name = info.get("name").toString();
				String lastModification = sdf.format(new Date(current.lastModified()));
				DataUnit ftSize = DataUnitHelper.fit(size);

				bfis.close();

				result.add(new TorrentQueueDto(name, ftSize.getValue(), ftSize.getUnit().getSymbol(), files.size(), lastModification, "NOT AVAIL."));
			} catch (Exception ex) {
				// TODO
			}
		}

		return result;
	}
}
