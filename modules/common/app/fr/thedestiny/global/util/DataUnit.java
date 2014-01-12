package fr.thedestiny.global.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;

/**
 * Classe gérant les unités de volume, permettant ainsi la conversion automatique
 * de mesures.
 * @author Sébastien
 */
public class DataUnit implements Comparable<DataUnit> {

	public final static long B = 1;
	public final static long KB = 1024;
	public final static long MB = KB * KB;
	public final static long GB = MB * KB;
	public final static long TB = GB * KB;

	private final static Map<String, Long> units = new HashMap<String, Long>();

	static {
		units.put("octets", B);
		units.put("Ko", KB);
		units.put("Mo", MB);
		units.put("Go", GB);
		units.put("To", TB);
	}

	@Getter
	private Double value = null;
	private Long unit = B;

	/**
	 * Constructeur
	 * @param value Bytes
	 */
	public DataUnit(Long value) {
		this.value = .0d;
		this.unit = B;

		for (long currentUnit : units.values()) {
			if (currentUnit > this.unit && value >= currentUnit) {
				this.value = (double) value / (double) currentUnit;
				this.unit = currentUnit;
			}
		}
	}

	/**
	 * Constructeur
	 * @param value
	 * @param unit
	 */
	public DataUnit(Double value, Long unit) {
		this.value = value;
		this.unit = unit;
	}

	public DataUnit(Double value, String unit) {
		this.value = value;
		this.unit = units.get(unit);
	}

	private Double convertInByte(Double value, Long unit) {
		return value * unit;
	}

	public String getUnit() {

		Set<Entry<String, Long>> unitEntries = units.entrySet();
		for (Entry<String, Long> current : unitEntries) {
			if (this.unit.equals(current.getValue())) {
				return current.getKey();
			}
		}

		return "?";
	}

	public Double getValue(String unit) {
		Double bytes = convertInByte(this.value, this.unit);

		Long divider = units.get(unit);
		if (divider == null) {
			return null;
		}

		return bytes / divider;
	}

	@Override
	public String toString() {
		return new DecimalFormat("# ##0,#").format(this.value) + " " + getUnit();
	}

	@Override
	public int compareTo(DataUnit other) {
		return (int) (convertInByte(this.value, this.unit) - convertInByte(other.value, other.unit));
	}
}
