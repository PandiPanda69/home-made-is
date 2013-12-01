package fr.thedestiny.torrent.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import play.db.jpa.JPA;
import fr.thedestiny.global.dao.AbstractDao;
import fr.thedestiny.global.util.TimeUnit;
import fr.thedestiny.torrent.model.Torrent;

public class TorrentDao extends AbstractDao<Torrent> {

	public static enum TorrentStatus {
		ALL, ACTIVE, DELETED
	};

	public TorrentDao(String persistenceContext) {
		super(persistenceContext);
	}

	@SuppressWarnings("unchecked")
	public List<Torrent> findAll(EntityManager em, TorrentStatus status) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		if (status == TorrentStatus.ALL) {
			return em.createQuery("from Torrent").getResultList();
		}

		return em.createQuery("from Torrent where status = :status")
				.setParameter("status", status.toString())
				.getResultList();
	}

	public Integer countRegisteredTorrent(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return (Integer) em.unwrap(Session.class).createSQLQuery("select count(*) from Torrent where status != 'DELETED'").uniqueResult();
	}

	public Integer countActiveTorrent(EntityManager em) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return (Integer) em.unwrap(Session.class).createSQLQuery("select count(distinct id_torrent) from TorrentStat where dat_snapshot >= datetime('now', '-1 months')").uniqueResult();
	}

	public Integer getDownloadedBytesOverTime(EntityManager em, int unitCount, TimeUnit unit) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		String unitString = getUnitString(unit);

		String sqlQuery = "select coalesce(sum(downloadedBytes),0) from Torrent where dat_creprod >= datetime('now', '-" + unitCount + " " + unitString + "')";
		return (Integer) em.unwrap(Session.class).createSQLQuery(sqlQuery).uniqueResult();
	}

	public Integer getUploadedBytesOverTime(EntityManager em, int unitCount, TimeUnit unit) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		String unitString = getUnitString(unit);

		String sqlQuery = "select coalesce(sum(A.uploadedBytes - B.uploadedBytes), 0) " + "from TorrentStat A " + "inner join TorrentStat B on A.id_torrent = B.id_torrent " + "where A.dat_snapshot >= datetime('now', '-" + unitCount + " " + unitString + "') " + "	and B.dat_snapshot = (" + "		select max(dat_snapshot) " + "		from TorrentStat " + "		where id_torrent = B.id_torrent " + "			and dat_snapshot < datetime('now', '-" + unitCount + " " + unitString + "')" + "	)";
		return (Integer) em.unwrap(Session.class).createSQLQuery(sqlQuery).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getLastTorrentActivityData(EntityManager em, int unitCount, TimeUnit unit, TorrentStatus status) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		String unitString = null;
		if(unit != TimeUnit.WEEK) {
			unitString = getUnitString(unit);
		}
		else {
			unitString = "days";
			unitCount = unitCount * 7;
		}

		String sqlQuery = "" +
				"select A.id_torrent as torrentId, A.dat_snapshot as lastActivityDate, cast(A.uploadedBytes as text) as uploadedBytes, cast((A.uploadedBytes - B.uploadedBytes) as text) as delta " +
				"from TorrentStat A " +
				"inner join TorrentStat B on A.id_torrent = B.id_torrent and B.dat_snapshot = (" +
				"		select min(dat_snapshot) " +
				"		from TorrentStat " +
				"		where id_torrent = A.id_torrent " +
				"			and dat_snapshot > datetime('now', '-" + unitCount + " " + unitString + "')) " +
				"where A.dat_snapshot = (select max(dat_snapshot) from TorrentStat where id_torrent = A.id_torrent)";

		if (status != TorrentStatus.ALL) {
			sqlQuery += " AND A.id_torrent IN (select id from Torrent where status = :status)";
		}

		Query q = em.unwrap(Session.class).createSQLQuery(sqlQuery);
		if (status != TorrentStatus.ALL) {
			q.setParameter("status", status.toString());
		}

		return q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
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
}
