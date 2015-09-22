package fr.thedestiny.message.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import fr.thedestiny.Constants;
import fr.thedestiny.global.dao.AbstractDao;
import fr.thedestiny.message.model.Contact;

@Repository
public class ContactDao extends AbstractDao<Contact> {

	protected ContactDao() {
		super(Constants.MESSAGES_CONTEXT, Contact.class);
	}

	@Override
	public List<Contact> findAll() {
		return em().createQuery("from Contact c order by c.name", Contact.class).getResultList();
	}

	public boolean delete(EntityManager em, final int id) {
		return em.createQuery("DELETE FROM Contact WHERE id = ?")
				.setParameter(1, id)
				.executeUpdate() == 1;
	}

	public int countContact() {
		return em().createQuery("select count(*) from Contact", Long.class)
				.getSingleResult()
				.intValue();
	}
}
