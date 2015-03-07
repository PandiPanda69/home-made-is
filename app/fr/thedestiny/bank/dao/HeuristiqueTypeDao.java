package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.HeuristiqueType;
import fr.thedestiny.global.dao.AbstractDao;

/**
 * Data Access Object du modèle HeuristiqueType
 * @author Sébastien
 */
@Repository
public class HeuristiqueTypeDao extends AbstractDao<HeuristiqueType> {

	public HeuristiqueTypeDao() {
		super("bank");
	}

	public HeuristiqueType findById(final int id) {
		return JPA.em().find(HeuristiqueType.class, id);
	}

	public HeuristiqueType findByNom(final String nom) {
		try {
			return JPA.em().createQuery("from HeuristiqueType where nom like ?", HeuristiqueType.class)
					.setParameter(1, nom)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public List<HeuristiqueType> findAll() {
		return JPA.em().createQuery("from HeuristiqueType", HeuristiqueType.class)
				.getResultList();
	}

	public HeuristiqueType save(final HeuristiqueType type) {

		if (type.getId() != null) {
			return JPA.em().merge(type);
		}

		JPA.em().persist(type);
		return type;
	}

	public boolean delete(final int id) {
		int result = JPA.em().createQuery("delete from HeuristiqueType where id = ?").setParameter(1, id).executeUpdate();
		return result == 1;
	}

	public void deleteAll() {
		JPA.em().createQuery("delete from HeuristiqueType").executeUpdate();
	}
}
