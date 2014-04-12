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

		Calendar expirationLimitCalendar = null;
		if (filterExpiredOnly) {
			expirationLimitCalendar = Calendar.getInstance();
			expirationLimitCalendar.add(Calendar.MONTH, -1);
		}

		for (TorrentStat current : stats) {
			TorrentDto dto = new TorrentDto(current.getTorrent());

			dto.setLastUpdateDate(current.getUnformattedLastActivityDate());

			DataUnit uploadedBytes = new DataUnit(current.getTotalUploaded());
			dto.setUploadedAmount(uploadedBytes.getValue());
			dto.setUploadedUnit(uploadedBytes.getUnit());

			if (current.getUploadedOnLastMonth() != null) {
				DataUnit delta = new DataUnit(current.getUploadedOnLastMonth());
				dto.setDeltaAmount(delta.getValue());
				dto.setDeltaUnit(delta.getUnit());
			}

			if (current.getTorrent().getDownloadedBytes() != 0) {
				double ratio = (double) current.getTotalUploaded() / (double) current.getTorrent().getDownloadedBytes();
				dto.setRatio(ratio);
			}

			// Gather expired torrents
			if (filterExpiredOnly) {
				if ((expirationLimitCalendar.getTimeInMillis() - current.getTorrent().getCreationDate().getTimeInMillis()) > 0) {
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
}
