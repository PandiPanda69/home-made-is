package fr.thedestiny.bank.dao;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import fr.thedestiny.bank.models.TauxInteret;
import fr.thedestiny.global.dao.AbstractDao;

@Repository
public class TauxInteretDao extends AbstractDao<TauxInteret> {

	private TauxInteretDao() {
		super("bank");
	}

	public void purge(EntityManager em, final int typeId) {
		em.createNativeQuery("DELETE FROM TauxInteret where id_type = :typeId").setParameter("typeId", typeId).executeUpdate();
	}

}
