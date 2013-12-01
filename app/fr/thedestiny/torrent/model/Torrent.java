package fr.thedestiny.torrent.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.ToString;
import play.Logger;

@Entity
@Data
@ToString
public class Torrent {

	@Id
	private Integer id;

	@Column
	private String name;

	@Column
	private Long downloadedBytes;

	@Column
	private String status;

	@Column(name = "dat_creprod")
	private String unformattedCreationDate;

	@Column(name = "dat_supprod")
	private String unformattedSuppressionDate;

	@OneToMany(mappedBy = "torrent")
	private List<TorrentStat> snaphots;

	public Torrent() {
	}

	public Calendar getCreationDate() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(unformattedCreationDate));
			return cal;
		} catch (ParseException ex) {
			Logger.error("Date conversion failed. ", ex);
			return null;
		}
	}

	public Calendar getSuppressionDate() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(unformattedSuppressionDate));
			return cal;
		} catch (ParseException ex) {
			Logger.error("Date conversion failed. ", ex);
			return null;
		}
	}
}
