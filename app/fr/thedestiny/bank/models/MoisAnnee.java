package fr.thedestiny.bank.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
public class MoisAnnee extends Model implements Serializable {

	private static final long serialVersionUID = -8349376534162763709L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(length = 2)
	@Getter
	@Setter
	private Integer mois;

	@Column(length = 4)
	@Getter
	@Setter
	private Integer annee;

	public MoisAnnee() {
	}

	public MoisAnnee(Integer id) {
		this.id = id;
	}

	/**
	 * Détermine si le mois courant est inférieur au mois passé en paramètre
	 * @param other
	 * @return
	 */
	public boolean lesserThan(MoisAnnee other) {

		if (annee < other.annee) {
			return true;
		} else if (annee > other.annee) {
			return false;
		}

		return (mois < other.mois);
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof MoisAnnee)) {
			return false;
		}

		MoisAnnee other = (MoisAnnee) obj;
		//		return (other.annee.equals(this.annee) && other.mois.equals(this.mois));
		return other.id.equals(this.id);
	}

	@Override
	public String toString() {
		return "MoisAnnee [id=" + id + ", mois=" + mois + ", annee=" + annee + "]";
	}
}
