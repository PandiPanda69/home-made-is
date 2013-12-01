package fr.thedestiny.torrent.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TorrentFilterDto extends AbstractDto {

	private String status;
	private Integer timeValue;
	private String timeUnit;

	public TorrentFilterDto() {
	}
}
