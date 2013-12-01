package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.NoResultException;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.HeuristiqueType;

/**
 * Data Access Object du modèle HeuristiqueType
 * @author Sébastien
 */
public class HeuristiqueTypeDao {

	public static HeuristiqueType findById(Integer id) {
		return JPA.em().find(HeuristiqueType.class, id);
	}

	public static HeuristiqueType findByNom(String nom) {
		try {
			return (HeuristiqueType) JPA.em().createQuery("from HeuristiqueType where nom like ?").setParameter(1, nom).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<HeuristiqueType> findAll() {
		return JPA.em().createQuery("from HeuristiqueType").getResultList();
	}

	public static HeuristiqueType save(HeuristiqueType type) {

		if (type.getId() != null) {
			return JPA.em().merge(type);
		}

		JPA.em().persist(type);
		return type;
	}

	public static void delete(HeuristiqueType type) {
		JPA.em().remove(type);
	}

	public static void delete(Integer id) throws Exception {
		int result = JPA.em().createQuery("delete from HeuristiqueType where id = ?").setParameter(1, id).executeUpdate();
		if (result == 0) {
			throw new Exception("Failure");
		}
	}

	public static void deleteAll() {
		JPA.em().createQuery("delete from HeuristiqueType").executeUpdate();
	}
}
