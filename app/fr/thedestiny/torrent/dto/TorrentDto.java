package fr.thedestiny.torrent.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.global.dto.AbstractDto;
import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.torrent.model.Torrent;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TorrentDto extends AbstractDto implements Comparable<TorrentDto> {

	private Integer id;
	private String name;
	private String status;
	private String creationDate;
	private String lastUpdateDate;
	private Double downloadedAmount;
	private String downloadedUnit;
	private Double uploadedAmount;
	private String uploadedUnit;
	private Double deltaAmount;
	private String deltaUnit;

	public TorrentDto() {
	}

	public TorrentDto(Torrent torrent) {

		this.id = torrent.getId();
		this.name = torrent.getName();
		this.status = torrent.getStatus();
		this.creationDate = torrent.getUnformattedCreationDate();

		DataUnit downloaded = new DataUnit(torrent.getDownloadedBytes());
		this.downloadedAmount = downloaded.getValue();
		this.downloadedUnit = downloaded.getUnit();
	}

	@Override
	public int compareTo(TorrentDto other) {

		if (this.deltaAmount == null)
			return -1;
		if (other.deltaAmount == null)
			return 1;

		DataUnit thisDelta = new DataUnit(this.deltaAmount, this.deltaUnit);
		DataUnit otherDelta = new DataUnit(other.deltaAmount, other.deltaUnit);
		return thisDelta.compareTo(otherDelta);
	}

	public Double getActivityRate() {
		DataUnit downloaded = new DataUnit(this.downloadedAmount, this.downloadedUnit);

		if (deltaAmount == null) {
			return null;
		}

		DataUnit delta = new DataUnit(this.deltaAmount, this.deltaUnit);

		Double downloadedBytes = downloaded.getValue("octets");
		Double deltaBytes = delta.getValue("octets");

		return deltaBytes / downloadedBytes * 100.0d;
	}
}