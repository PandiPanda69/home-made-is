package fr.thedestiny.torrent.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.thedestiny.auth.security.Security;
import fr.thedestiny.global.helper.ResultFactory;
import fr.thedestiny.torrent.dto.TorrentDto;
import fr.thedestiny.torrent.dto.TorrentFilterDto;
import fr.thedestiny.torrent.service.TorrentService;

@org.springframework.stereotype.Controller
public class TorrentsController extends Controller {

	@Autowired
	private TorrentService torrentService;

	@Autowired
	private ObjectMapper objectMapper;

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
	public Result filter() {

		try {
			TorrentFilterDto dto = objectMapper.readValue(ctx().request().body().asJson().toString(), TorrentFilterDto.class);
			List<TorrentDto> torrents = torrentService.findAllTorrentsMatchingFilter(dto);

			return ok(Json.toJson(torrents));
		} catch (IOException ex) {
			Logger.error("Error while unserializing JSON", ex);
			return ResultFactory.FAIL;
		}
	}

	@Transactional
	@Security
	@SuppressWarnings("unchecked")
	public Result update(Integer torrentId) {

		try {
			Map<String, Object> data = objectMapper.readValue(ctx().request().body().asJson().toString(), Map.class);
			Integer grade = (Integer) data.get("grade");

			torrentService.updateTorrentGrade(torrentId, grade);
		} catch (IOException ex) {
			Logger.error("Error while unserializing JSON", ex);
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
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
