package fr.thedestiny.message.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.Constants;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;
import fr.thedestiny.message.dao.ContactDao;
import fr.thedestiny.message.dao.PhoneDao;
import fr.thedestiny.message.model.Contact;
import fr.thedestiny.message.model.Phone;

@Service
public class ContactService extends AbstractService {

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private PhoneDao phoneDao;

	public ContactService() {
		super(Constants.MESSAGES_CONTEXT);
	}

	public List<Contact> findContacts() {
		return contactDao.findAll();
	}

	public Contact updateContact(final Contact contact) {

		return processInTransaction(new InTransactionFunction<Contact>() {

			@Override
			public Contact doWork(EntityManager em) throws Exception {
				Contact persisted = contactDao.findById(em, contact.getId());
				persisted.setName(contact.getName());

				// Désattachement d'un numéro impossible donc pas géré.
				List<Integer> phonesToFetch = new ArrayList<>();
				for (final Phone newPhone : contact.getPhones()) {
					boolean existing = false;
					for (final Phone oldPhone : persisted.getPhones()) {
						if (oldPhone.getId() == newPhone.getId()) {
							existing = true;
							break;
						}
					}

					if (!existing) {
						phonesToFetch.add(newPhone.getId());
					}
				}

				if (!phonesToFetch.isEmpty()) {
					List<Phone> phones = phoneDao.findByIds(phonesToFetch);
					persisted.getPhones().addAll(phones);
				}

				return persisted;
			}
		});
	}

	public boolean deleteContact(final Integer id) {
		return processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws Exception {
				return contactDao.delete(em, id);
			}
		});
	}
}
