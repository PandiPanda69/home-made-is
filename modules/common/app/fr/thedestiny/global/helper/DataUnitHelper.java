package fr.thedestiny.global.helper;

import java.math.BigDecimal;

import fr.thedestiny.global.util.DataUnit;
import fr.thedestiny.global.util.DataUnit.Unit;

/**
 * @author Sébastien
 */
public class DataUnitHelper {

	/**
	 * Constructor : This class cannot be instantiated.
	 */
	private DataUnitHelper() {
	}

	/**
	 * Convert a value to another unit (ex: KB -> MB)  
	 * @param value Value to be converted
	 * @param toUnit New unit
	 * @return The new value
	 */
	public static DataUnit convert(final DataUnit value, final Unit toUnit) {

		if (Unit.BYTES.equals(toUnit)) {
			return new DataUnit(value.getInBytes());
		}

		double converted = BigDecimal.valueOf(value.getInBytes()).divide(BigDecimal.valueOf(toUnit.getValue())).doubleValue();
		return new DataUnit(converted, toUnit);
	}

	/**
	 * Fit value with the best unit
	 * @param value {@link DataUnit} to be fit
	 * @return The fit value
	 * @see DataUnitHelper#fit(long)
	 */
	public static DataUnit fit(final DataUnit value) {
		return fit(value.getInBytes());
	}

	/**
	 * Fit value with the best unit
	 * @param value Amount of <em>unit</em> to be best fit.
	 * @param unit Unit of <value>
	 * @return New instance of {@link DataUnit}
	 */
	public static DataUnit fit(final double value, final Unit unit) {
		return fit((long) (value * unit.getValue()));
	}

	/**
	 * Instantiate a fit {@link DataUnit} of <em>value</em> bytes.
	 * @param value Value in bytes that must be fit
	 * @return New instance of {@link DataUnit}
	 */
	public static DataUnit fit(final long value) {

		if (value == 0) {
			return new DataUnit(0l);
		}

		Unit matchingUnit = Unit.BYTES;

		for (Unit unit : Unit.values()) {
			if (value < unit.getValue()) {
				break;
			}

			matchingUnit = unit;
		}

		BigDecimal converted = BigDecimal.valueOf(value);
		double result = converted.divide(BigDecimal.valueOf(matchingUnit.getValue())).doubleValue();

		return new DataUnit(result, matchingUnit);
	}
}
