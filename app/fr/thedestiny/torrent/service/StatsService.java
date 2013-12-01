package fr.thedestiny.torrent.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.dao.TorrentDao;
import fr.thedestiny.torrent.dao.TorrentDao.TorrentStatus;
import fr.thedestiny.torrent.dto.HomeStatsDto;
import fr.thedestiny.torrent.model.Torrent;
import fr.thedestiny.torrent.model.TorrentStat;

public class StatsService {

	private static StatsService thisInstance = new StatsService();

	private TorrentDao torrentDao = null;

	public static StatsService getInstance() {
		return thisInstance;
	}

	private StatsService() {
		torrentDao = new TorrentDao("torrent");
	}

	public HomeStatsDto getStatsForHomepage() {
		HomeStatsDto dto = new HomeStatsDto();

		dto.setRegisteredTorrents(torrentDao.countRegisteredTorrent(null));
		dto.setInactiveTorrents(dto.getRegisteredTorrents() - torrentDao.countActiveTorrent(null));

		DataUnit downloaded = new DataUnit(Long.valueOf(torrentDao.getDownloadedBytesOverTime(null, 1, TimeUnit.DAY)));
		dto.setDownloadedAmount(downloaded.getValue());
		dto.setDownloadedUnit(downloaded.getUnit());

		DataUnit uploaded = new DataUnit(Long.valueOf(torrentDao.getUploadedBytesOverTime(null, 1, TimeUnit.DAY)));
		dto.setUploadedAmount(uploaded.getValue());
		dto.setUploadedUnit(uploaded.getUnit());

		List<Torrent> torrents = torrentDao.findAll(null, TorrentStatus.ALL);

		// Algo foireux !
		Map<String, Long> activity = new HashMap<String, Long>();
		for (Torrent current : torrents) {
			for (Entry<String, Long> entry : computeTorrentUploadStats(current).entrySet()) {
				if (activity.containsKey(entry.getKey())) {
					Long currentValue = activity.get(entry.getKey());
					currentValue += entry.getValue();

					activity.put(entry.getKey(), currentValue);
				} else {
					activity.put(entry.getKey(), entry.getValue());
				}
			}
		}

		List<Entry<String, Long>> elementList = new ArrayList<Entry<String, Long>>(activity.entrySet());
		Collections.sort(elementList, new Comparator<Entry<String, Long>>() {

			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {

				String[] array1 = o1.getKey().split("/");
				String[] array2 = o2.getKey().split("/");

				int yearComparison = array1[2].compareTo(array2[2]);
				if (yearComparison == 0) {
					int monthComparison = array1[1].compareTo(array2[1]);
					if (monthComparison == 0) {
						int dayComparison = array1[0].compareTo(array2[0]);
						return dayComparison;
					}

					return monthComparison;
				}

				return yearComparison;
			}
		});

		dto.getGraph().setPointStart(elementList.get(0).getKey());
		dto.getGraph().setPointInterval(1000L * 60 * 60 * 24);

		for (Entry<String, Long> entry : elementList) {
			dto.getGraph().getElements().add(entry.getValue());
		}

		return dto;
	}

	private Map<String, Long> computeTorrentUploadStats(Torrent torrent) {
		HashMap<String, Long> result = new HashMap<String, Long>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		List<TorrentStat> snapshots = torrent.getSnaphots();
		Collections.sort(snapshots);

		Calendar calendarInstance = Calendar.getInstance();
		Calendar lastDate = null;
		Long lastUploaded = 0L;
		for (TorrentStat current : snapshots) {
			if (lastDate != null) {
				Calendar snapshotDate = current.getSnapshotDate();
				Calendar nextTheoricalDate = calendarInstance;
				nextTheoricalDate.setTime(lastDate.getTime());
				nextTheoricalDate.add(Calendar.DAY_OF_MONTH, 1);

				snapshotDate.set(snapshotDate.get(Calendar.YEAR), snapshotDate.get(Calendar.MONTH), snapshotDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				nextTheoricalDate.set(nextTheoricalDate.get(Calendar.YEAR), nextTheoricalDate.get(Calendar.MONTH), nextTheoricalDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

				// Si le jour suivant dans la liste n'est pas le N+1
				// on met des zéros dans le résultat jusqu'au jour J :-)
				if (snapshotDate.after(nextTheoricalDate)) {
					while (snapshotDate.after(nextTheoricalDate)) {
						result.put(formatter.format(nextTheoricalDate.getTime()), 0L);
						nextTheoricalDate.add(Calendar.DAY_OF_MONTH, 1);
					}
				}

				String formattedDate = formatter.format(snapshotDate.getTime());
				if (result.containsKey(formattedDate)) {
					Long delta = (current.getUploadedBytes() - lastUploaded) + result.get(formattedDate);
					result.put(formattedDate, delta);
				} else {
					Long delta = current.getUploadedBytes() - lastUploaded;
					result.put(formatter.format(snapshotDate.getTime()), delta);
				}
			}

			lastDate = current.getSnapshotDate();
			lastUploaded = current.getUploadedBytes();
		}

		return result;
	}
}
