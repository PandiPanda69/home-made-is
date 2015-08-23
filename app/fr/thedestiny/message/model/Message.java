package fr.thedestiny.message.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;

@Entity
@Getter
public class Message {

	@Id
	private int id;

	@ManyToOne(targetEntity = Phone.class)
	@JoinColumn(name = "phone_id")
	private Phone phone;

	@Column
	private Direction direction;

	@Column
	private MessageStatus status;

	@Column
	private String body;

	@Column(name = "dat_msg")
	private Date date;
}
