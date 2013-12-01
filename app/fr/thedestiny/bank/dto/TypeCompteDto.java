package fr.thedestiny.bank.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.bank.models.TauxInteret;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class TypeCompteDto extends AbstractDto {

	private Integer id;
	private String libelle;
	private String type;
	private List<TauxInteretDto> taux;

	public TypeCompteDto(final TypeCompte model) {
		this.id = model.getId();
		this.libelle = model.getLibelle();
		this.type = model.getType();

		this.taux = new ArrayList<TauxInteretDto>();
		if (model.getTaux() != null) {
			for (TauxInteret current : model.getTaux()) {
				TauxInteretDto dto = new TauxInteretDto();
				dto.setId(current.getId());
				dto.setDate(current.getDate().toString().substring(0, 10));
				dto.setRate(current.getRate());
				this.taux.add(dto);
			}
		}
	}
}
