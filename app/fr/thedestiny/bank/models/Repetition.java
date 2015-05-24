package fr.thedestiny.bank.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class Repetition implements Serializable {

	private static final long serialVersionUID = 9144643960576878209L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(value = AccessLevel.NONE)
	private Integer id;

	@Column(length = 35, nullable = false)
	private String nom;

	@Column(precision = 2)
	private Double montant;

	@Column
	private Boolean active;

	@ManyToOne(targetEntity = Compte.class)
	private Compte compte;

	@ManyToOne(targetEntity = OperationType.class)
	private OperationType type;
}
