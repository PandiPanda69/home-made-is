package fr.thedestiny.bank.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Operation extends Model implements Serializable {

	private static final long serialVersionUID = -9150979970986152095L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.NONE)
	private Integer id;

	@Column(length = 40, nullable = false)
	private String nom;

	@Column(precision = 2, nullable = false)
	private Double montant;

	@Column(length = 150)
	private String nomComplet;

	@ManyToOne(targetEntity = Compte.class)
	private Compte compte;

	@ManyToOne(targetEntity = MoisAnnee.class)
	private MoisAnnee mois;

	@ManyToOne(targetEntity = OperationType.class)
	private OperationType type;

	@Column
	private String raison;

	@Column
	private Date date;

	public Operation() {
	}
}
