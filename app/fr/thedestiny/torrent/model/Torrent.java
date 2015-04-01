package fr.thedestiny.torrent.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;
import play.Logger;

@Entity
@Data
@ToString
public class Torrent {

	private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	@Id
	private Integer id;

	@Column
	private String name;

	@Column
	private Long downloadedBytes;

	@Column
	private Long uploadedBytes;

	@Column
	private String status;

	@Column
	private Integer grade;

	@Column(name = "dat_creprod")
	private String unformattedCreationDate;

	@Column(name = "dat_supprod")
	private String unformattedSuppressionDate;

	@Column
	private String trackerError;

	public Torrent() {
	}

	// TODO Why ?!
	public Calendar getCreationDate() {
		try {
			GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+1"));
			cal.setTime(new SimpleDateFormat(DATE_FORMAT).parse(unformattedCreationDate));
			return cal;
		} catch (ParseException ex) {
			Logger.error("Date conversion failed. ", ex);
			return null;
		}
	}

	// TODO Why ?!
	public Calendar getSuppressionDate() {
		try {
			GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+1"));
			cal.setTime(new SimpleDateFormat(DATE_FORMAT).parse(unformattedSuppressionDate));
			return cal;
		} catch (ParseException ex) {
			Logger.error("Date conversion failed. ", ex);
			return null;
		}
	}

	public void setSuppressionDate(final Date date) {
		this.unformattedSuppressionDate = new SimpleDateFormat(DATE_FORMAT).format(date);
	}
}
