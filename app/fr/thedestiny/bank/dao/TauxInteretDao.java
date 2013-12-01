package fr.thedestiny.bank.dao;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import fr.thedestiny.bank.models.TauxInteret;
import fr.thedestiny.global.dao.AbstractDao;

public class TauxInteretDao extends AbstractDao<TauxInteret> {

	public TauxInteretDao(String persistenceContext) {
		super(persistenceContext);
	}

	public void purge(EntityManager em, Integer typeId) {
		((Session) em.unwrap(Session.class)).createSQLQuery("DELETE FROM TauxInteret where id_type = :typeId").setParameter("typeId", typeId).executeUpdate();
	}

}
