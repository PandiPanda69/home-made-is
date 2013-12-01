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
public class OperationType extends Model implements Serializable {

	private static final long serialVersionUID = 569668476965284820L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(length = 25, nullable = false, unique = true)
	@Getter
	@Setter
	private String name;

	@Column(length = 15)
	@Getter
	@Setter
	private String icon;

	public OperationType() {
	}

	public OperationType(String id) {
		this.id = Integer.parseInt(id);
	}
}
