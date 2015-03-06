package fr.thedestiny.auth.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import fr.thedestiny.global.model.Model;

@Entity
@AllArgsConstructor
public class Module extends Model implements Serializable, Comparable<Module> {

	private static final long serialVersionUID = 5426014245037531997L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(name = "nom")
	@Getter
	@Setter
	private String name;

	@Column
	@Getter
	@Setter
	private String desc;

	@Column
	@Getter
	@Setter
	private String route;

	@Column
	@Getter
	@Setter
	private boolean active;

	public Module() {
	}

	@Override
	public int compareTo(Module o) {
		return this.name.compareTo(o.name);
	}
}
