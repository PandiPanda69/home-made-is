package fr.thedestiny.message.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.Constants;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionProcedure;
import fr.thedestiny.message.dao.MessageDao;
import fr.thedestiny.message.dao.PhoneDao;
import fr.thedestiny.message.dto.ContactMessages;
import fr.thedestiny.message.model.Contact;
import fr.thedestiny.message.model.Direction;
import fr.thedestiny.message.model.Message;
import fr.thedestiny.message.model.MessageStatus;
import fr.thedestiny.message.model.Phone;

@Service
public class MessageService extends AbstractService {

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private ContactService contactService;

	@Autowired
	private PhoneDao phoneDao;

	private MessageService() {
		super(Constants.MESSAGES_CONTEXT);
	}

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

	public void pushMessages(final List<HashMap<String, Object>> messages) {

		for (Map<String, Object> current : messages) {
			String address = (String) current.get("address");
			String body = (String) current.get("body");
			long date = (Long) current.get("date");
			Direction direction = current.get("type").equals(1) ? Direction.IN : Direction.OUT;
			MessageStatus status = current.get("seen").equals(1) ? MessageStatus.READ : MessageStatus.UNREAD;

			// Replace +33 prefix by a 0.
			String strippedAddress = address.replace("+33", "0");
			Phone phone = phoneDao.findByPhone(strippedAddress);
			if (phone == null) {
				final Contact contact = contactService.addContact(address, strippedAddress);
				phone = contact.getPhones().get(0);
			}

			final Message message = new Message();
			message.setBody(body);
			message.setDate(new Date(date));
			message.setDirection(direction);
			message.setStatus(status);
			message.setPhone(phone);

			processInTransaction(new InTransactionProcedure() {

				@Override
				public void doWork(EntityManager em) throws Exception {
					messageDao.save(em, message);
				}
			});
		}
	}
}