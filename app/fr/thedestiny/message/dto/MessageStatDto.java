package fr.thedestiny.message.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.thedestiny.global.dto.GraphDto;

@Getter
@Setter
public class MessageStatDto {

	private int contactCount;
	private int phoneCount;
	private int messageCount;

	private GraphDto<List<Long>> graphData = new GraphDto<>();
}
