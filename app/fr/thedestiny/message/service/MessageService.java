package fr.thedestiny.message.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.message.dao.MessageDao;
import fr.thedestiny.message.dto.ContactMessages;
import fr.thedestiny.message.model.Message;

@Service
public class MessageService {

	@Autowired
	private MessageDao messageDao;

	public List<Message> findAll() {
		return messageDao.findAll();
	}

	public List<ContactMessages> findCountByContact() {

		@SuppressWarnings("unchecked")
		List<Object[]> raw = (List<Object[]>) messageDao.findCountByContact();

		List<ContactMessages> result = new ArrayList<>(raw.size());
		for (Object[] current : raw) {
			ContactMessages dto = new ContactMessages((int) current[0], (String) current[1], (long) current[2], (Date) current[3]);
			result.add(dto);
		}

		return result;
	}

	public List<Message> findByContact(final int id) {

		return messageDao.findByContact(id);
	}
}
