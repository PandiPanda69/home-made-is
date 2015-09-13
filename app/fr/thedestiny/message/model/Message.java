package fr.thedestiny.message.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(targetEntity = Phone.class)
	@JoinColumn(name = "phone_id")
	@Setter
	private Phone phone;

	@Column
	@Enumerated(EnumType.STRING)
	@Setter
	private Direction direction;

	@Column
	@Enumerated(EnumType.STRING)
	@Setter
	private MessageStatus status;

	@Column
	@Setter
	private String body;

	@Column(name = "dat_msg")
	@Setter
	private Date date;
}
