package fr.thedestiny.bank.dto;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CompteDto extends AbstractDto {

	private Integer id;
	private String nom;
	private Double solde;
	private Date lastUpdate;
	private Integer typeId;
	private String type;
	private String category;

	public CompteDto(Compte compte) {
		this.id = compte.getId();
		this.nom = compte.getNom();
		this.typeId = compte.getType().getId();
		this.type = compte.getType().getLibelle();
		this.category = compte.getType().getType();
		this.lastUpdate = compte.getLastUpdate();
		this.solde = compte.getSolde();
	}
}
