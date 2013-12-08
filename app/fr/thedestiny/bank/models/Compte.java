package fr.thedestiny.bank.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import fr.thedestiny.global.model.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
public class Compte extends Model implements Serializable {

	private static final long serialVersionUID = 7793161045204950986L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(length = 35, nullable = false)
	@Getter
	@Setter
	private String nom;

	@Column(precision = 2)
	@Getter
	@Setter
	private Double solde;

	@Column
	@Getter
	@Setter
	private Date lastUpdate;

	@Column(name = "owner_id")
	@Getter
	@Setter
	private Integer owner;

	@ManyToMany
	@JoinTable(name = "CompteMois", joinColumns = { @JoinColumn(name = "compte_id") }, inverseJoinColumns = { @JoinColumn(name = "mois_id") })
	@OrderBy("annee, mois")
	@Getter
	@Setter
	private List<MoisAnnee> mois = new ArrayList<MoisAnnee>();

	@ManyToOne(targetEntity = TypeCompte.class)
	@JoinColumn(name = "id_type")
	@Getter
	@Setter
	private TypeCompte type;

	public Compte() {
	}

	public Compte(Integer id) {
		this.id = id;
	}

	/**
	 * Renvoie vrai si le mois est déjà affecté au compte 
	 * @param mois
	 * @return
	 */
	public boolean isMonthLinked(MoisAnnee mois) {

		for (MoisAnnee current : this.mois) {
			if (current.getId().equals(mois.getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retourne le mois venant après celui passé en paramètre
	 * @param mois
	 * @return
	 */
	public MoisAnnee getNextMonth(MoisAnnee ref) {

		MoisAnnee result = null;
		for (MoisAnnee current : mois) {

			// Result < Current < Ref 
			if ((result == null || current.lesserThan(result)) && ref.lesserThan(current)) {
				result = current;
			}
		}

		return result;
	}
}
