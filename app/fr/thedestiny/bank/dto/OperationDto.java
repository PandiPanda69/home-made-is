package fr.thedestiny.bank.dto;

import java.text.SimpleDateFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.models.OperationType;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class OperationDto extends AbstractDto {

	private final static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	private Integer id;
	private String nom;
	private Double montant;
	private String nomComplet;
	private String date;
	private String raison;
	private Integer moisAnneeId;
	private Integer compteId;
	private OperationType type;

	public OperationDto() {
	}

	public OperationDto(Operation operation) {
		this.id = operation.getId();
		this.nom = operation.getNom();
		this.nomComplet = operation.getNomComplet();
		this.montant = operation.getMontant();
		this.raison = operation.getRaison();
		this.moisAnneeId = operation.getMois().getId();
		this.compteId = operation.getCompte().getId();
		this.type = operation.getType();

		if (operation.getDate() != null) {
			this.date = formatter.format(operation.getDate());
		}
	}

	public Operation toBO() {
		Operation op = new Operation(id, nom, montant, nomComplet, null, null, type, null, null);
		op.setCompte(new Compte(compteId));
		op.setMois(new MoisAnnee(moisAnneeId));

		return op;
	}
}