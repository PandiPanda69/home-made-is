package fr.thedestiny.torrent.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import play.Logger;

@Table(name = "DailyTorrentStats")
@Entity
@Data
public class TorrentStat {

	@Id
	private Integer id;

	@OneToOne(targetEntity = Torrent.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_torrent")
	private Torrent torrent;

	@Column(name = "dat_active")
	private String unformattedLastActivityDate;

	@Column(name = "uploaded_total")
	private Long totalUploaded;

	@Column(name = "uploaded_period")
	private Long uploadedOnLastMonth;

	public TorrentStat() {
	}

	// TODO Why?!
	public Calendar getLastActivityDate() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(unformattedLastActivityDate));
			return cal;
		} catch (ParseException ex) {
			Logger.error("Date conversion failed. ", ex);
			return null;
		}
	}
}
