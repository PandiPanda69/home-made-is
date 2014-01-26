package fr.thedestiny.torrent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class TorrentQueueDto {

	private String name;
	private Double sizeAmount;
	private String sizeUnit;
	private Integer fileCount;
	private String creationDate;
	private String status;

}
