package fr.thedestiny.global.util;

import java.text.DecimalFormat;

import lombok.Getter;

public class DataUnit implements Comparable<DataUnit> {

	public static enum Unit {

		BYTES("octets", 1L),
		KILOBYTES("ko", 1024L),
		MEGABYTES("Mo", 1024L * 1024L),
		GIGABYTES("Go", 1024L * 1024L * 1024L),
		TERABYTES("To", 1024L * 1024L * 1024L * 1024L);

		@Getter
		private final String symbol;
		@Getter
		private final long value;

		private Unit(final String symbol, final long value) {
			this.symbol = symbol;
			this.value = value;
		}

		public static Unit fromSymbol(final String symbol) {
			Unit result = null;

			for (Unit unit : Unit.values()) {
				if (unit.getSymbol().equals(symbol)) {
					result = unit;
					break;
				}
			}

			return result;
		}

		public static Unit fromValue(final long value) {
			Unit result = null;

			for (Unit unit : Unit.values()) {
				if (unit.getValue() == value) {
					result = unit;
					break;
				}
			}

			return result;
		}
	}

	@Getter
	private final double value;
	@Getter
	private final Unit unit;

	public DataUnit(final long value) {
		this.value = value;
		this.unit = Unit.BYTES;
	}

	public DataUnit(final double value, final Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public long getInBytes() {
		return (long) (this.value * this.unit.getValue());
	}

	@Override
	public String toString() {
		return DecimalFormat.getInstance().format(this.value) + " " + getUnit().getSymbol();
	}

	@Override
	public int compareTo(final DataUnit other) {
		long diff = (getInBytes() - other.getInBytes());

		if (diff > 0) {
			return 1;
		} else if (diff < 0) {
			return -1;
		}

		return 0;
	}
}
