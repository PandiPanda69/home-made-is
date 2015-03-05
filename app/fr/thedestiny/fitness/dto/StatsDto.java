package fr.thedestiny.fitness.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class StatsDto extends AbstractDto {

	@Data
	public static class StatsElement {

		private Long timestamp;
		private Double val;

		public StatsElement(final long timestamp, final double val) {
			this.timestamp = timestamp;
			this.val = val;
		}
	}

	private List<StatsElement> elements = new ArrayList<>();

	public void addStat(final long timestamp, final double val) {
		elements.add(new StatsElement(timestamp, val));
	}

}
