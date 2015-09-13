package fr.thedestiny.message.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Contact_Phone")
@Getter
public class Phone {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column
	@Setter
	private String phone;

	@Column(name = "contact_id")
	private int contactId;

	public Phone() {
	}

	public void setContact(final Contact contact) {
		this.contactId = contact.getId();
	}
}
