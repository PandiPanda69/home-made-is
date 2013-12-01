package fr.thedestiny.auth.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import fr.thedestiny.bank.models.Model;

@Entity
@AllArgsConstructor
public class Utilisateur extends Model implements Serializable {

	private static final long serialVersionUID = 3107018075639038485L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	@Column(length = 25, nullable = false, unique = true)
	@Getter
	@Setter
	private String username;

	@Column(length = 45)
	@Getter
	@Setter
	private String password;

	@Column(length = 35)
	@Getter
	@Setter
	private String firstName;

	@Column(columnDefinition = "boolean default false")
	@Getter
	@Setter
	public Boolean isAdmin;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "Privilege", joinColumns = @JoinColumn(name = "id_utilisateur"), inverseJoinColumns = @JoinColumn(name = "id_module"))
	@Getter
	@Setter
	public Set<Module> privileges;

	public Utilisateur() {

	}
}
