package fr.thedestiny.torrent.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.Constants;
import fr.thedestiny.global.dao.SolrSearchDao;
import fr.thedestiny.global.exception.CoreNotFoundException;
import fr.thedestiny.global.helper.DataUnitHelper;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;
import fr.thedestiny.global.service.InTransactionProcedure;
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

	@Autowired
	private SolrSearchDao searchDao;

	protected TorrentService() {
		super(Constants.TORRENT_CONTEXT);
	}

	public List<TorrentDto> findAllTorrentsMatchingFilter(final TorrentFilterDto filter) {

		TorrentStatus statusFilter = TorrentStatus.valueOf(filter.getStatus());
		boolean filterExpiredOnly = TorrentStatus.EXPIRED.equals(statusFilter);

		List<TorrentStat> stats = torrentDao.getLastTorrentActivityData(null, statusFilter);

		return convertTorrentStat(stats, filterExpiredOnly);
	}

	public boolean deleteTorrent(final int torrentId) {
		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) {
				return torrentDao.logicalDelete(em, torrentId);
			}
		});
	}

	public void updateTorrentGrade(final int torrentId, final Integer grade) {
		this.processInTransaction(new InTransactionProcedure() {

			@Override
			public void doWork(EntityManager em) {
				Torrent torrent = torrentDao.find(em, torrentId);
				torrent.setGrade(grade);
			}
		});
	}

	public int countInactiveTorrents() {
		int result = 0;
		List<TorrentStat> rawTorrents = torrentDao.getLastTorrentActivityData(null, TorrentStatus.EXPIRED);

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

	protected List<TorrentDto> convertTorrentStat(final List<TorrentStat> stats, final boolean filterExpiredOnly) {

		long expirationLimitTime = 0L;

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

	public List<TorrentDto> findTorrents(final String value, String status) throws SolrServerException {

		boolean filterExpiredOnly = false;
		Map<String, String> criteria = new HashMap<>();

		if (TorrentStatus.EXPIRED.name().equals(status)) {
			status = TorrentStatus.ACTIVE.name();
			filterExpiredOnly = true;
		}

		// If all status included, no clause needed.
		if (!TorrentStatus.ALL.name().equals(status)) {
			criteria.put("status", status);
		}

		criteria.put("name", value);

		SolrDocumentList docs;
		try {
			docs = searchDao.search(Constants.TORRENT_CONTEXT, criteria);
		} catch (CoreNotFoundException ex) {
			Logger.error("Code fault.", ex);
			return null;
		}

		List<Integer> ids = new ArrayList<>();
		if (docs.size() == 0) {
			return Collections.emptyList();
		}

		for (SolrDocument doc : docs) {
			ids.add(Integer.valueOf(doc.get("id").toString()));
		}

		List<TorrentStat> stats = torrentDao.getTorrentActivityDataById(ids);

		return convertTorrentStat(stats, filterExpiredOnly);
	}
}
