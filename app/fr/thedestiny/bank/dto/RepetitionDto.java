package fr.thedestiny.bank.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.thedestiny.bank.models.Repetition;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class RepetitionDto extends AbstractDto implements Serializable {

	private static final long serialVersionUID = 8567315367073654372L;

	private int id;
	private String nom;
	private double montant;
	private boolean active;
	private String type;

	public RepetitionDto(final Repetition bean) {

		this.id = bean.getId();
		this.nom = bean.getNom();
		this.montant = bean.getMontant();
		this.active = bean.getActive();
		this.type = bean.getType().getName();
	}
}
