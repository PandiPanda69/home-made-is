package fr.thedestiny.message.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column
	@Setter
	private String name;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, targetEntity = Phone.class)
	@JoinColumn(name = "contact_id")
	private List<Phone> phones;

	public Contact() {
	}

	public Contact(final String name) {
		this.name = name;
	}

	public List<Phone> getPhones() {
		if (phones == null) {
			phones = new ArrayList<>();
		}

		return phones;
	}
}
