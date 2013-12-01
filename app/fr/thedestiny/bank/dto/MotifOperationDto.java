package fr.thedestiny.bank.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MotifOperationDto extends AbstractDto {

	private Integer id;
	private String motif;

	public MotifOperationDto(MotifOperation model) {
		this.id = model.getId();
		this.motif = model.getMotif();
	}

}
