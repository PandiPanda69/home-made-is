package fr.thedestiny.torrent.service;

import java.util.ArrayList;
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

		List<TorrentDto> result = new ArrayList<TorrentDto>();

		List<TorrentStat> stats = torrentDao.getLastTorrentActivityData(null, filter.getTimeValue(), unitFilter, statusFilter);

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

			double ratio = (double) current.getTotalUploaded() / (double) current.getTorrent().getDownloadedBytes();
			dto.setRatio(ratio);

			result.add(dto);
		}

		Collections.sort(result);

		return result;
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
