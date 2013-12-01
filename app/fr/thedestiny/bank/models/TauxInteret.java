package fr.thedestiny.bank.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import lombok.Data;

@Entity
@Data
public class TauxInteret {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@OrderBy
	@Column(name = "dat_deb")
	private Date date;

	@Column(name = "taux")
	private Double rate;

	@ManyToOne(targetEntity = TypeCompte.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_type")
	private TypeCompte type;

	public TauxInteret() {
	}
}
