package fr.thedestiny.torrent.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.global.helper.DataUnitHelper;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.dao.TorrentDao;
import fr.thedestiny.torrent.dao.TorrentDao.StatType;
import fr.thedestiny.torrent.dao.TorrentDirectoryDao;
import fr.thedestiny.torrent.dto.HomeStatsDto;

@Service
public class StatsService {

	@Autowired
	private TorrentDao torrentDao;

	@Autowired
	private TorrentDirectoryDao directoryDao;

	@Autowired
	private TorrentService torrentService;

	protected StatsService() {
	}

	// TODO : Refacto !!
	public HomeStatsDto getStatsForHomepage() {
		HomeStatsDto dto = new HomeStatsDto();

		dto.setRegisteredTorrents(torrentDao.countRegisteredTorrent(null).intValue());
		dto.setInactiveTorrents(torrentService.countInactiveTorrents());

		List<Map<String, Object>> uploadStats = torrentDao.getTorrentStatHistory(StatType.UPLOAD, 4, TimeUnit.MONTH);
		List<Map<String, Object>> downloadStats = torrentDao.getTorrentStatHistory(StatType.DOWNLOAD, 4, TimeUnit.MONTH);

		dto.getUploadGraph().setPointStart(uploadStats.get(0).get("dat_stat").toString());
		dto.getUploadGraph().setPointInterval(1000L * 60 * 60 * 24);

		dto.getDownloadGraph().setPointStart(uploadStats.get(0).get("dat_stat").toString());
		dto.getDownloadGraph().setPointInterval(1000L * 60 * 60 * 24);

		Map<String, Object> lastUploadedStat = null;
		for (Map<String, Object> current : uploadStats) {
			dto.getUploadGraph().getElements().add(Long.valueOf(current.get("byteAmount").toString()));
			lastUploadedStat = current;
		}

		Map<String, Object> lastDownloadedStat = null;
		for (Map<String, Object> current : downloadStats) {
			dto.getDownloadGraph().getElements().add(Long.valueOf(current.get("byteAmount").toString()));
			lastDownloadedStat = current;
		}

		String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (lastDownloadedStat == null || !currentDate.equals(lastDownloadedStat.get("dat_stat").toString())) {
			dto.setDownloadedAmount(.0d);
			dto.setDownloadedUnit(DataUnit.Unit.BYTES.getSymbol());
		}
		else {
			DataUnit downloaded = DataUnitHelper.fit(Long.valueOf(lastDownloadedStat.get("byteAmount").toString()));
			dto.setDownloadedAmount(downloaded.getValue());
			dto.setDownloadedUnit(downloaded.getUnit().getSymbol());
		}

		if (lastUploadedStat == null || !currentDate.equals(lastUploadedStat.get("dat_stat").toString())) {
			dto.setUploadedAmount(.0d);
			dto.setUploadedUnit(DataUnit.Unit.BYTES.getSymbol());
		}
		else {
			DataUnit uploaded = DataUnitHelper.fit(Long.valueOf(lastUploadedStat.get("byteAmount").toString()));
			dto.setUploadedAmount(uploaded.getValue());
			dto.setUploadedUnit(uploaded.getUnit().getSymbol());
		}

		DataUnit freeSpace = directoryDao.getFreeSpaceInTargetDirectory();
		dto.setSpaceLeftOnDeviceAmount(freeSpace.getValue());
		dto.setSpaceLeftOnDeviceUnit(freeSpace.getUnit().getSymbol());

		return dto;
	}
}
