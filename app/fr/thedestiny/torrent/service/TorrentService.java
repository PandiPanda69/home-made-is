package fr.thedestiny.torrent.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.global.helper.DataUnitHelper;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionProcedure;
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

	protected TorrentService() {
		super("torrent");
	}

	public List<TorrentDto> findAllTorrentsMatchingFilter(final TorrentFilterDto filter) {

		TorrentStatus statusFilter = TorrentStatus.valueOf(filter.getStatus());
		TimeUnit unitFilter = TimeUnit.valueOf(filter.getTimeUnit());

		List<TorrentStat> stats = torrentDao.getLastTorrentActivityData(null, filter.getTimeValue(), unitFilter, statusFilter);

		long expirationLimitTime = 0L;
		boolean filterExpiredOnly = TorrentStatus.EXPIRED.equals(statusFilter);

		if (filterExpiredOnly) {
			expirationLimitTime = getTorrentExpirationTimestamp();
		}

		List<TorrentDto> torrents = new ArrayList<>();
		for (TorrentStat current : stats) {
			// Gather expired torrents
			if (filterExpiredOnly) {
				if (isTorrentInactive(current.getTorrent(), expirationLimitTime)) {
					torrents.add(buildTorrentDto(current));
				}
			}
			else {
				torrents.add(buildTorrentDto(current));
			}
		}

		Collections.sort(torrents);

		return torrents;
	}

	public void deleteTorrent(final Integer torrentId) {
		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) throws Exception {
				torrentDao.logicalDelete(em, torrentId);
			}
		});
	}

	public void updateTorrentGrade(final Integer torrentId, final Integer grade) {
		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) throws Exception {
				Torrent torrent = torrentDao.find(em, torrentId);
				torrent.setGrade(grade);
			}
		});
	}

	public int countInactiveTorrents(final int timeValue, final TimeUnit timeUnit) {
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

	public boolean isTorrentInactive(final Torrent torrent, final long expirationLimitTime) {
		long torrentCreationDate = torrent.getCreationDate().getTimeInMillis();
		String trackerError = torrent.getTrackerError();

		return ((expirationLimitTime - torrentCreationDate) > 0) || (trackerError != null && !trackerError.isEmpty());
	}

	protected long getTorrentExpirationTimestamp() {
		Calendar expirationLimitCalendar = Calendar.getInstance();
		expirationLimitCalendar.add(Calendar.MONTH, -1);

		return expirationLimitCalendar.getTimeInMillis();
	}

	protected TorrentDto buildTorrentDto(final TorrentStat torrentStat) {
		TorrentDto dto = new TorrentDto(torrentStat.getTorrent());

		dto.setLastUpdateDate(torrentStat.getUnformattedLastActivityDate());
		dto.setUploadedData(DataUnitHelper.fit(torrentStat.getTotalUploaded()));

		if (torrentStat.getUploadedOnLastMonth() != null) {
			dto.setDeltaData(DataUnitHelper.fit(torrentStat.getUploadedOnLastMonth()));
		}

		if (torrentStat.getTorrent().getDownloadedBytes() != 0) {
			double ratio = (double) torrentStat.getTotalUploaded() / (double) torrentStat.getTorrent().getDownloadedBytes();
			dto.setRatio(ratio);
		}

		return dto;
	}
}
