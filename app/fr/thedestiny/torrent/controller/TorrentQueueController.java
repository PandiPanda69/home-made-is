package fr.thedestiny.torrent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import fr.thedestiny.torrent.dto.TorrentQueueDto;
import fr.thedestiny.torrent.service.TorrentQueueService;

@Controller
public class TorrentQueueController extends play.mvc.Controller {

	@Autowired
	private TorrentQueueService queueService;

	public Result list() {

		List<TorrentQueueDto> dto = null;
		try {
			dto = queueService.findQueuedTorrents();
		} catch (Exception ex) {
			Logger.error("Error occured:", ex);
		}

		return ok(Json.toJson(dto));
	}
}
