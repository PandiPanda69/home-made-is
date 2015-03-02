package fr.thedestiny.torrent.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import fr.thedestiny.torrent.model.Torrent;

public class TorrentServiceTest {

	private TorrentService service = new TorrentService();

	@Test
	public void testTorrentActive() {
		Torrent torrent = new Torrent();

		torrent.setUnformattedCreationDate("2025-11-11 00:00:00");
		torrent.setTrackerError("");

		assertFalse(service.isTorrentInactive(torrent, new Date().getTime()));
	}

	@Test
	public void testTorrentInactive() {
		Torrent torrent = new Torrent();

		torrent.setUnformattedCreationDate("2015-01-15 00:00:00");
		torrent.setTrackerError("");

		assertTrue(service.isTorrentInactive(torrent, new Date().getTime()));

		torrent.setTrackerError("ERROR");

		assertTrue(service.isTorrentInactive(torrent, new Date().getTime()));

		torrent.setUnformattedCreationDate("2025-01-15 00:00:00");

		assertTrue(service.isTorrentInactive(torrent, new Date().getTime()));
	}
}
