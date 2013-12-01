package fr.thedestiny.bank.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

//@Entity
@AllArgsConstructor
@ToString
public class HeuristiqueOperation extends Model {

	//	@Id
	//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Integer id;

	public HeuristiqueOperation() {
	}
}
