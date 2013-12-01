package fr.thedestiny.bank.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TauxInteretDto extends AbstractDto {

	private Integer id;
	private String date;
	private Double rate;

	public TauxInteretDto() {
	}
}
