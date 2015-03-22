package fr.thedestiny.bank.dto;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.global.dto.AbstractDto;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class SearchResultDto extends AbstractDto {

	private String nom;
	private String montant;
	private String nomComplet;
	private String moisAnnee;
	private String type;

	public SearchResultDto() {
	}

	public SearchResultDto(final Operation operation) {
		this.nom = operation.getNom();
		this.nomComplet = operation.getNomComplet();
		this.montant = formatAmount(operation.getMontant());
		this.moisAnnee = formatYearMonth(operation.getMois());

		if (operation.getType() != null) {
			this.type = operation.getType().getName();
		} else {
			this.type = StringUtils.EMPTY;
		}
	}

	private String formatAmount(final double amount) {
		return DecimalFormat.getCurrencyInstance().format(amount);
	}

	private String formatYearMonth(final MoisAnnee moisAnnee) {
		String month = DateFormatSymbols.getInstance().getMonths()[moisAnnee.getMois()];

		return StringUtils.capitalize(month) + " " + moisAnnee.getAnnee();
	}
}