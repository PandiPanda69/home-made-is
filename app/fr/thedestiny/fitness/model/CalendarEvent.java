package fr.thedestiny.fitness.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString
@EqualsAndHashCode
public class CalendarEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column
	private Date date;

	@Column(name = "user_id")
	private Integer userId;

	@OneToOne(mappedBy = "calendarEvent", cascade = CascadeType.ALL)
	private Weight weight;

	public CalendarEvent() {
	}
}
