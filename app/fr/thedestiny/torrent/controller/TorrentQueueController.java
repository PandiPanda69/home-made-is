package fr.thedestiny.torrent.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import fr.thedestiny.global.helper.ResultFactory;
import fr.thedestiny.torrent.dto.TorrentQueueDto;
import fr.thedestiny.torrent.service.TorrentQueueService;

@Controller
public class TorrentQueueController extends play.mvc.Controller {

	@Autowired
	private TorrentQueueService queueService;

	public Result list() {

		try {
			List<TorrentQueueDto> dto = queueService.findQueuedTorrents();
			return ok(Json.toJson(dto));
		} catch (FileNotFoundException ex) {
			Logger.error("Invalid conf.", ex);
			return ResultFactory.FAIL;
		}
	}
}
