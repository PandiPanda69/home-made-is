package fr.thedestiny.torrent.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import play.Logger;

@Data
@Entity
@ToString
public class TorrentStat implements Comparable<TorrentStat>, Serializable {

	private static final long serialVersionUID = 1937599376032L;

	@Id
	private Integer id;

	@Column(name = "dat_snapshot")
	@Getter(AccessLevel.NONE)
	private String unformatedSnapshotDate;

	@Column
	private Long uploadedBytes;

	private transient Calendar snapshotDate = null;

	@ManyToOne(targetEntity = Torrent.class)
	@JoinColumn(name = "id_torrent", referencedColumnName = "id")
	private Torrent torrent;

	public TorrentStat() {
	}

	public Calendar getSnapshotDate() {
		if (snapshotDate == null) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(unformatedSnapshotDate));

				this.snapshotDate = cal;
			} catch (ParseException ex) {
				Logger.error("Date conversion failed. ", ex);
				return null;
			}
		}

		return snapshotDate;
	}

	@Override
	public int compareTo(TorrentStat other) {
		return this.getSnapshotDate().compareTo(other.getSnapshotDate());
	}
}
