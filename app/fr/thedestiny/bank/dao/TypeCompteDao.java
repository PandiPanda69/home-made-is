package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.global.dao.AbstractDao;

public class TypeCompteDao extends AbstractDao<TypeCompte> {

	public TypeCompteDao(String persistenceContext) {
		super(persistenceContext);
	}

	@SuppressWarnings("unchecked")
	public List<TypeCompte> findAll(EntityManager em) {

		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("FROM TypeCompte").getResultList();
	}

	public boolean isTypeInUse(Integer typeId) {

		Session session = JPA.em(persistenceContext).unwrap(Session.class);
		Integer count = (Integer) session.createSQLQuery("SELECT COUNT(1) FROM Compte WHERE id_type = :typeId").setParameter("typeId", typeId).uniqueResult();

		return count > 0;
	}

	public void delete(EntityManager em, Integer typeId) throws Exception {
		int result = em.createQuery("delete from TypeCompte where id = ?").setParameter(1, typeId).executeUpdate();
		if (result == 0) {
			throw new Exception("Failure");
		}
	}
}
