package fr.thedestiny.bank.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
public class Solde extends Model implements Serializable {

	private static final long serialVersionUID = 111348886208097520L;

	@Id
	@ManyToOne(targetEntity = Compte.class)
	@Getter
	@Setter
	private Compte compte;

	@Id
	@ManyToOne(targetEntity = MoisAnnee.class)
	@Getter
	@Setter
	private MoisAnnee mois;

	@Column(precision = 2, nullable = false)
	@Getter
	@Setter
	private Double solde;

	public Solde() {
	}

	@Override
	public String toString() {
		return "Solde [compte=" + compte + ", mois=" + mois + ", solde=" + solde + "]";
	}
}
