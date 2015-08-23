package fr.thedestiny.message.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "Contact_Phone")
@Getter
public class Phone {

	@Id
	private int id;

	@Column
	private String phone;

	@Column
	private int contact_id;

	public Phone() {
	}
}
