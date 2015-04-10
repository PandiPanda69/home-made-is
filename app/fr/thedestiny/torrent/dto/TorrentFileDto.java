package fr.thedestiny.torrent.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.thedestiny.global.dto.AbstractDto;
import fr.thedestiny.global.helper.DataUnitHelper;

@Getter
@Setter
public class TorrentFileDto extends AbstractDto {

	private String filename;
	private String size;
	private List<TorrentFileDto> children;

	public TorrentFileDto(final String filename, final Long size) {
		this.filename = filename;
		this.size = DataUnitHelper.fit(size).toString();
	}
}
