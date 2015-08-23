package fr.thedestiny.message.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import fr.thedestiny.Constants;
import fr.thedestiny.global.dao.AbstractDao;
import fr.thedestiny.message.model.Phone;

@Repository
public class PhoneDao extends AbstractDao<Phone> {

	public PhoneDao() {
		super(Constants.MESSAGES_CONTEXT, Phone.class);
	}

	public List<Phone> findByIds(List<Integer> ids) {
		return em().createQuery("FROM Phone WHERE id IN :ids", Phone.class)
				.setParameter("ids", ids)
				.getResultList();
	}
}
