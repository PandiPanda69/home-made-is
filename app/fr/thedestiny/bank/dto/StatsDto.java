package fr.thedestiny.bank.dto;

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

		private Integer key;
		private Object val;

		public StatsElement(Integer key, Object val) {
			this.key = key;
			this.val = val;
		}
	}

	private Integer accountId;
	private List<StatsElement> elements = new ArrayList<StatsElement>();

	public void addStat(Integer key, Object val) {
		elements.add(new StatsElement(key, val));
	}

}
