package fr.thedestiny.torrent.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.Constants;
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
		super(Constants.TORRENT_CONTEXT);
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

	public List<TorrentStat> getLastTorrentActivityData(EntityManager em, TorrentStatus status) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		String query = "from TorrentStat s join fetch s.torrent t";
		if (status != TorrentStatus.ALL) {
			query += " WHERE t.status = :status";

			if (status == TorrentStatus.EXPIRED) {
				query += " AND ((s.uploadedOnLastMonth = null OR s.uploadedOnLastMonth = 0)";
				query += " OR length(t.trackerError) > 0)";
			}
		}

		TypedQuery<TorrentStat> q = em.createQuery(query, TorrentStat.class);
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

	private String getUnitString(final TimeUnit unit) {
		switch (unit) {
		case MONTH:
			return "months";
		case DAY:
			return "days";
		default:
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getTorrentStatHistory(final StatType type, int unitCount, final TimeUnit unit) {

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

	public List<TorrentStat> getTorrentActivityDataById(final List<Integer> ids) {

		String query = "from TorrentStat s join fetch s.torrent t WHERE t.id IN :ids";

		TypedQuery<TorrentStat> q = JPA.em(persistenceContext).createQuery(query, TorrentStat.class);
		q.setParameter("ids", ids);

		return q.getResultList();
	}

	public void cleanTorrentStat(EntityManager em, final int torrentId) {

		String sql = "DELETE FROM TorrentStat WHERE id_torrent = ?";

		em
		.createNativeQuery(sql)
		.setParameter(1, torrentId)
		.executeUpdate();
	}

	public Long getTotalUploadedBytes(EntityManager em, final int torrentId) {
		try {
			return em
					.createQuery("select s.totalUploaded from TorrentStat s join s.torrent t where t.id = 442 order by s.unformattedLastActivityDate desc", Long.class)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
}
