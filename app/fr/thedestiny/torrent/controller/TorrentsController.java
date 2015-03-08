package fr.thedestiny.torrent.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
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
	public Result update(final Integer torrentId) {

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
	public Result delete(final Integer torrentId) {

		if (!torrentService.deleteTorrent(torrentId)) {
			return ResultFactory.FAIL;
		}

		return ResultFactory.OK;
	}

	@Security
	public Result find(final String value) {

		Map<String, String[]> params = ctx().request().queryString();
		if (params == null) {
			return badRequest();
		}

		if (!params.containsKey("status") || params.get("status").length != 1) {
			return badRequest();
		}

		String status = params.get("status")[0];

		try {
			List<TorrentDto> dto = torrentService.findTorrents(value, status);
			return ok(Json.toJson(dto));
		} catch (SolrServerException ex) {
			Map<String, String> error = new HashMap<>();
			error.put("message", ex.getMessage());

			return internalServerError(Json.toJson(error));
		}
	}
}
