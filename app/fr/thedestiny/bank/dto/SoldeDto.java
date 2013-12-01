package fr.thedestiny.bank.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.bank.models.Solde;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class SoldeDto extends AbstractDto {

	private Integer moisAnneId;
	private Double solde;
	private Integer compteId;

	public SoldeDto(Solde solde) {
		this.moisAnneId = solde.getMois().getId();
		this.compteId = solde.getCompte().getId();
		this.solde = solde.getSolde();
	}
}