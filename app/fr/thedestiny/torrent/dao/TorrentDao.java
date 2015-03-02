package fr.thedestiny.torrent.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.global.dao.AbstractDao;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.model.Torrent;
import fr.thedestiny.torrent.model.TorrentStat;

@Repository
public class TorrentDao extends AbstractDao<Torrent> {

	public static enum TorrentStatus {
		ALL, ACTIVE, DELETED, EXPIRED
	};

	public static enum StatType {
		DOWNLOAD, UPLOAD
	};

	protected TorrentDao() {
		super("torrent");
	}

	public List<Torrent> findAll(EntityManager em, final TorrentStatus status) {

		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		if (status == TorrentStatus.ALL) {
			return em.createQuery("from Torrent", Torrent.class).getResultList();
		}
		else if (status == TorrentStatus.EXPIRED) {
			throw new UnsupportedOperationException("Status EXPIRED is not supported for this operation.");
		}

		return em.createQuery("from Torrent where status = :status", Torrent.class)
				.setParameter("status", status.toString())
				.getResultList();
	}

	public Torrent find(EntityManager em, final int torrentId) {
		return findById(em, torrentId, Torrent.class);
	}

	public Long countRegisteredTorrent(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return (Long) em.createQuery("select count(t) from Torrent t where status != :status")
				.setParameter("status", TorrentStatus.DELETED.name())
				.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<TorrentStat> getLastTorrentActivityData(EntityManager em, int unitCount, TimeUnit unit, TorrentStatus status) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		// TODO Disable temporally
		//		String unitString = null;
		//		if (unit != TimeUnit.WEEK) {
		//			unitString = getUnitString(unit);
		//		}
		//		else {
		//			unitString = "days";
		//			unitCount = unitCount * 7;
		//		}

		String query = "from TorrentStat s join fetch s.torrent t";
		if (status != TorrentStatus.ALL) {
			query += " WHERE t.status = :status";

			if (status == TorrentStatus.EXPIRED) {
				query += " AND ((s.uploadedOnLastMonth = null OR s.uploadedOnLastMonth = 0)";
				query += " OR length(t.trackerError) > 0)";
			}
		}

		Query q = em.createQuery(query, TorrentStat.class);
		if (status != TorrentStatus.ALL) {
			if (status == TorrentStatus.EXPIRED) {
				q.setParameter("status", TorrentStatus.ACTIVE.toString());
			}
			else {
				q.setParameter("status", status.toString());
			}
		}

		return q.getResultList();
	}

	private String getUnitString(TimeUnit unit) {
		switch (unit) {
		case MONTH:
			return "months";
		case DAY:
			return "days";
		}

		throw new UnsupportedOperationException();
	}

	public void delete(EntityManager em, Integer torrentId) {
		em.createQuery("delete from TorrentStat where id_torrent = :torrentId").setParameter("torrentId", torrentId).executeUpdate();
		em.createQuery("delete from Torrent where id = :torrentId").setParameter("torrentId", torrentId).executeUpdate();
	}

	public void logicalDelete(EntityManager em, Integer torrentId) {
		em.createQuery("update Torrent set status = 'DELETED' where id = :torrentId").setParameter("torrentId", torrentId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getTorrentStatHistory(StatType type, int unitCount, TimeUnit unit) {

		String unitString = null;
		if (unit != TimeUnit.WEEK) {
			unitString = getUnitString(unit);
		}
		else {
			unitString = "days";
			unitCount = unitCount * 7;
		}

		// TODO HQL use
		String sqlQuery = "select dat_stat, cast(byteAmount as text) as byteAmount " +
				"from DailyStats " +
				"where type = :type and dat_stat >= date(current_date, '-" + unitCount + " " + unitString + "') " +
				"order by dat_stat";

		return JPA.em(persistenceContext)
				.unwrap(Session.class)
				.createSQLQuery(sqlQuery)
				.setParameter("type", type.name())
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
	}
}
