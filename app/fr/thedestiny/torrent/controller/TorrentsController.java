package fr.thedestiny.torrent.controller;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import fr.thedestiny.auth.security.Security;
import fr.thedestiny.global.helper.ResultFactory;
import fr.thedestiny.torrent.dto.TorrentDto;
import fr.thedestiny.torrent.dto.TorrentFilterDto;
import fr.thedestiny.torrent.service.TorrentService;

@org.springframework.stereotype.Controller
public class TorrentsController extends Controller {

	@Autowired
	private TorrentService torrentService;

	@Transactional(readOnly = true)
	@Security
	public Result list() {

		// Create default filter
		TorrentFilterDto dto = new TorrentFilterDto();
		dto.setStatus("ACTIVE");
		dto.setTimeValue(1);
		dto.setTimeUnit("MONTH");

		List<TorrentDto> torrents = torrentService.findAllTorrentsMatchingFilter(dto);
		return ok(Json.toJson(torrents));
	}

	@Transactional(readOnly = true)
	@Security
	public Result filter() throws Exception {

		TorrentFilterDto dto = new ObjectMapper().readValue(ctx().request().body().asJson(), TorrentFilterDto.class);
		List<TorrentDto> torrents = torrentService.findAllTorrentsMatchingFilter(dto);

		return ok(Json.toJson(torrents));
	}

	@Transactional
	@Security
	public Result delete(Integer torrentId) {

		try {
			torrentService.deleteTorrent(torrentId);
		} catch (Throwable ex) {
			Logger.error("Error : ", ex);
			return ResultFactory.FAIL;
		}
		return ResultFactory.OK;
	}
}
