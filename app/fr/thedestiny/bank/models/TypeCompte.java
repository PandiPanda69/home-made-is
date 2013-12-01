package fr.thedestiny.bank.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TypeCompte extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column
	private String libelle;

	@Column
	private String type;

	@OneToMany(targetEntity = TauxInteret.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_type")
	private List<TauxInteret> taux;

	public TypeCompte() {
	}
}
