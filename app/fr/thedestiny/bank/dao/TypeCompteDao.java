package fr.thedestiny.bank.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import play.db.jpa.JPA;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.global.dao.AbstractDao;

@Repository
public class TypeCompteDao extends AbstractDao<TypeCompte> {

	private TypeCompteDao() {
		super("bank", TypeCompte.class);
	}

	public List<TypeCompte> findAll(EntityManager em) {

		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.createQuery("FROM TypeCompte", TypeCompte.class).getResultList();
	}

	public boolean isTypeInUse(final int typeId) {

		int count = JPA.em(persistenceContext)
				.createNamedQuery("SELECT COUNT(1) FROM Compte WHERE id_type = :typeId", Integer.class)
				.setParameter("typeId", typeId)
				.getSingleResult();

		return count > 0;
	}

	public boolean delete(EntityManager em, final int typeId) {
		int result = em.createQuery("delete from TypeCompte where id = ?").setParameter(1, typeId).executeUpdate();
		return result == 1;
	}
}
