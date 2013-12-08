package fr.thedestiny.bank.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fr.thedestiny.global.model.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
public class HeuristiqueType extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(length = 40, nullable = false)
	@Getter
	@Setter
	private String nom;

	@ManyToOne(targetEntity = OperationType.class, fetch = FetchType.EAGER)
	@Getter
	@Setter
	private OperationType type;

	@Column
	@Getter
	@Setter
	private Double threshold;

	public HeuristiqueType() {
	}

	@Override
	public String toString() {
		return "HeuristiqueType [id=" + id + ", nom=" + nom + ", type=" + type + ", threshold=" + threshold + "]";
	}
}
