package fr.thedestiny.torrent.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.dao.TorrentDao;
import fr.thedestiny.torrent.dao.TorrentDao.TorrentStatus;
import fr.thedestiny.torrent.dto.TorrentDto;
import fr.thedestiny.torrent.dto.TorrentFilterDto;
import fr.thedestiny.torrent.model.Torrent;

public class TorrentService extends AbstractService {

	private static TorrentService thisInstance = new TorrentService();

	private TorrentDao torrentDao = null;

	public static TorrentService getInstance() {
		return thisInstance;
	}

	private TorrentService() {
		super("torrent");
		this.torrentDao = new TorrentDao("torrent");
	}

	public List<TorrentDto> findAllTorrentsMatchingFilter(TorrentFilterDto filter) {

		TorrentStatus statusFilter = TorrentStatus.valueOf(filter.getStatus());
		TimeUnit unitFilter = TimeUnit.valueOf(filter.getTimeUnit());

		List<TorrentDto> result = new ArrayList<TorrentDto>();

		List<Torrent> torrents = torrentDao.findAll(null, statusFilter);
		List<Map<String, Object>> activityData = torrentDao.getLastTorrentActivityData(null, filter.getTimeValue(), unitFilter, statusFilter);

		for (Torrent current : torrents) {
			TorrentDto dto = new TorrentDto(current);

			Map<String, Object> data = null;
			for (Map<String, Object> currentData : activityData) {
				if (((Integer) currentData.get("torrentId")).equals(current.getId())) {
					data = currentData;
					break;
				}
			}

			if (data != null) {
				activityData.remove(data);

				dto.setLastUpdateDate((String) data.get("lastActivityDate"));

				DataUnit uploadedBytes = new DataUnit(Long.valueOf((String) data.get("uploadedBytes")));
				dto.setUploadedAmount(uploadedBytes.getValue());
				dto.setUploadedUnit(uploadedBytes.getUnit());

				DataUnit delta = new DataUnit(Long.valueOf((String) data.get("delta")));
				dto.setDeltaAmount(delta.getValue());
				dto.setDeltaUnit(delta.getUnit());
			}

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
}
