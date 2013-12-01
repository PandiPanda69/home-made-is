package fr.thedestiny.auth.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import fr.thedestiny.auth.model.Module;
import fr.thedestiny.auth.model.Utilisateur;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends AbstractDto implements Serializable {

	private static final long serialVersionUID = 3618632370287309556L;

	public Integer id;
	public String username;
	public String password;
	public String firstName;
	public boolean isAdmin;

	private List<Integer> modules;

	public UserDto() {
	}

	public UserDto(Utilisateur u) {
		this.setId(u.getId());
		this.setUsername(u.getUsername());
		this.setFirstName(u.getFirstName());
		this.setAdmin(u.getIsAdmin());

		this.modules = new ArrayList<Integer>();

		for (Module current : u.getPrivileges()) {
			modules.add(current.getId());
		}
	}
}
