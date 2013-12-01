package fr.thedestiny.bank.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@ToString
public class MotifOperation extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column
	@Getter
	@Setter
	private String motif;

	@Column(name = "user_id")
	@Getter
	@Setter
	private Integer userId;

	public MotifOperation() {
	}
}
