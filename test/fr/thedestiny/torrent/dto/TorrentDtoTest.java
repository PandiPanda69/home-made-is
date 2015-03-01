package fr.thedestiny.torrent.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TorrentDtoTest {

	private static final double DOWNLOADED = 10d;
	private static final double DELTA = 1d;

	private TorrentDto dto;

	@Before
	public void before() {
		dto = new TorrentDto();

		dto.setDownloadedAmount(DOWNLOADED);
		dto.setDownloadedUnit("Mo");

		dto.setDeltaAmount(DELTA);
		dto.setDeltaUnit("Mo");
	}

	@Test
	public void testActivityRate() {
		assertEquals(DELTA / DOWNLOADED * 100, dto.getActivityRate(), 0);
	}

	@Test
	public void testCompareTo() {
		TorrentDto dto2 = new TorrentDto();

		dto2.setDeltaAmount(DOWNLOADED);
		dto2.setDeltaUnit("ko");

		assertTrue(dto2.compareTo(dto) < 0);
	}
}
