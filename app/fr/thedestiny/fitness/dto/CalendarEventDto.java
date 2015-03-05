package fr.thedestiny.fitness.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.fitness.model.CalendarEvent;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CalendarEventDto extends AbstractDto {

	private Integer id;
	private Long timestamp;
	private Double weight;

	public CalendarEventDto() {
	}

	public CalendarEventDto(final CalendarEvent event) {
		this.id = event.getId();
		this.timestamp = event.getDate().getTime();
		this.weight = (event.getWeight() == null) ? null : event.getWeight().getValue();
	}
}
