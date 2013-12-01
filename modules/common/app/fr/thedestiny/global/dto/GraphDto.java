package fr.thedestiny.global.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class GraphDto<T> extends AbstractDto {

	private String pointStart;
	private Long pointInterval;
	private List<T> elements = new ArrayList<T>();

	public void addStat(T val) {
		elements.add(val);
	}

}
