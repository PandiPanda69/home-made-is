package fr.thedestiny.torrent.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.thedestiny.global.dto.AbstractDto;
import fr.thedestiny.global.dto.GraphDto;

@Data
@EqualsAndHashCode(callSuper = false)
public class HomeStatsDto extends AbstractDto {

	private int registeredTorrents;
	private int inactiveTorrents;

	private Double downloadedAmount;
	private String downloadedUnit;

	private Double uploadedAmount;
	private String uploadedUnit;

	private GraphDto<Long> uploadGraph = new GraphDto<Long>();
	private GraphDto<Long> downloadGraph = new GraphDto<Long>();

	private Double spaceLeftOnDeviceAmount;
	private String spaceLeftOnDeviceUnit;

	public HomeStatsDto() {
	}
}
