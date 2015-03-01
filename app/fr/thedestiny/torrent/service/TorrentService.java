package fr.thedestiny.torrent.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.DataUnitHelper;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.dao.TorrentDao;
import fr.thedestiny.torrent.dao.TorrentDao.TorrentStatus;
import fr.thedestiny.torrent.dto.TorrentDto;
import fr.thedestiny.torrent.dto.TorrentFilterDto;
import fr.thedestiny.torrent.model.Torrent;
import fr.thedestiny.torrent.model.TorrentStat;

@Service
public class TorrentService extends AbstractService {

	@Autowired
	private TorrentDao torrentDao;

	private TorrentService() {
		super("torrent");
	}

	public List<TorrentDto> findAllTorrentsMatchingFilter(TorrentFilterDto filter) {

		TorrentStatus statusFilter = TorrentStatus.valueOf(filter.getStatus());
		TimeUnit unitFilter = TimeUnit.valueOf(filter.getTimeUnit());

		boolean filterExpiredOnly = (statusFilter == TorrentStatus.EXPIRED);

		List<TorrentDto> torrents = new ArrayList<TorrentDto>();

		List<TorrentStat> stats = torrentDao.getLastTorrentActivityData(null, filter.getTimeValue(), unitFilter, statusFilter);

		long expirationLimitTime = 0;
		if (filterExpiredOnly) {
			expirationLimitTime = getTorrentExpirationTimestamp();
		}

		for (TorrentStat current : stats) {
			TorrentDto dto = new TorrentDto(current.getTorrent());

			dto.setLastUpdateDate(current.getUnformattedLastActivityDate());

			DataUnit uploadedBytes = DataUnitHelper.fit(current.getTotalUploaded());
			dto.setUploadedAmount(uploadedBytes.getValue());
			dto.setUploadedUnit(uploadedBytes.getUnit().getSymbol());

			if (current.getUploadedOnLastMonth() != null) {
				DataUnit delta = DataUnitHelper.fit(current.getUploadedOnLastMonth());
				dto.setDeltaAmount(delta.getValue());
				dto.setDeltaUnit(delta.getUnit().getSymbol());
			}

			if (current.getTorrent().getDownloadedBytes() != 0) {
				double ratio = (double) current.getTotalUploaded() / (double) current.getTorrent().getDownloadedBytes();
				dto.setRatio(ratio);
			}

			// Gather expired torrents
			if (filterExpiredOnly) {
				if (isTorrentInactive(current.getTorrent(), expirationLimitTime)) {
					torrents.add(dto);
				}
			}
			else {
				torrents.add(dto);
			}
		}

		Collections.sort(torrents);

		return torrents;
	}

	public void deleteTorrent(final Integer torrentId) throws Throwable {
		this.processInTransaction(new InTransactionAction() {

			@Override
			public <T> T doWork(EntityManager em) throws Exception {
				torrentDao.logicalDelete(em, torrentId);
				return null;
			}
		});
	}

	public void updateTorrentGrade(final Integer torrentId, final Integer grade) throws Exception {
		this.processInTransaction(new InTransactionAction() {

			@Override
			public <T> T doWork(EntityManager em) throws Exception {
				Torrent torrent = torrentDao.find(em, torrentId);
				torrent.setGrade(grade);

				return null;
			}
		});
	}

	public int countInactiveTorrents(int timeValue, TimeUnit timeUnit) {
		int result = 0;
		List<TorrentStat> rawTorrents = torrentDao.getLastTorrentActivityData(null, timeValue, timeUnit, TorrentStatus.EXPIRED);

		long expirationLimitTime = getTorrentExpirationTimestamp();

		for (TorrentStat current : rawTorrents) {
			if (isTorrentInactive(current.getTorrent(), expirationLimitTime)) {
				result++;
			}
		}

		return result;
	}

	public boolean isTorrentInactive(Torrent torrent, long expirationLimitTime) {
		long torrentCreationDate = torrent.getCreationDate().getTimeInMillis();
		String trackerError = torrent.getTrackerError();

		return ((expirationLimitTime - torrentCreationDate) > 0) || (trackerError != null && trackerError.length() > 0);
	}

	private long getTorrentExpirationTimestamp() {
		Calendar expirationLimitCalendar = Calendar.getInstance();
		expirationLimitCalendar.add(Calendar.MONTH, -1);

		return expirationLimitCalendar.getTimeInMillis();
	}
}
