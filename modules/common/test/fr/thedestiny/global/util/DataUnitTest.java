package fr.thedestiny.global.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.thedestiny.global.util.DataUnit.Unit;

public class DataUnitTest {

	@Test
	public void testFromBytes() {
		DataUnit _10MB = new DataUnit(10L * 1024 * 1024);

		assertEquals(10L * 1024 * 1024, _10MB.getValue(), .0d);
		assertEquals(Unit.BYTES, _10MB.getUnit());
	}

	@Test
	public void testFromMBtoBytes() {
		DataUnit unit = new DataUnit(10.66d, Unit.MEGABYTES);

		assertEquals((long) (10.66d * 1024 * 1024), unit.getInBytes());
	}

	@Test
	public void testFromMBtoGB() {
		DataUnit unit = new DataUnit(10.66d * 1024, Unit.MEGABYTES);
		assertEquals(10.66d * 1024, unit.getValue(), .0d);
		assertEquals(Unit.MEGABYTES, unit.getUnit());

		DataUnit converted = DataUnitHelper.convert(unit, Unit.GIGABYTES);
		assertEquals(10.66d, converted.getValue(), 1e-6);
		assertEquals(Unit.GIGABYTES, converted.getUnit());
	}

	@Test
	public void testCompareDataUnit() {
		DataUnit unit10 = new DataUnit(10 * 1024 * 1024);
		DataUnit unit50 = new DataUnit(50 * 1024 * 1024);

		assertTrue(unit10.compareTo(unit50) < 0);
	}

	@Test
	public void testToString() {
		DataUnit unit = new DataUnit(50, Unit.MEGABYTES);
		assertEquals("50 Mo", unit.toString());
	}

	@Test
	public void testFitExistingDataUnit() {
		DataUnit fit = DataUnitHelper.fit(new DataUnit(50d * 1024, Unit.GIGABYTES));

		assertEquals(50d, fit.getValue(), 1e-6);
		assertEquals(Unit.TERABYTES, fit.getUnit());
	}

	@Test
	public void testFitBytes() {
		DataUnit fit = DataUnitHelper.fit((long) (44.6d * 1024 * 1024 * 1024));

		assertEquals(44.6d, fit.getValue(), 1e-6);
		assertEquals(Unit.GIGABYTES, fit.getUnit());
	}

	@Test
	public void testFitValueAndUnit() {
		DataUnit fit = DataUnitHelper.fit(43.76d * 1024, Unit.KILOBYTES);

		assertEquals(43.76d, fit.getValue(), 1e-6);
		assertEquals(Unit.MEGABYTES, fit.getUnit());
	}
}
