package fr.thedestiny.global.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import play.Configuration;
import play.Play;
import fr.thedestiny.Constants;
import fr.thedestiny.global.exception.CoreNotFoundException;

public class SolrSearchDao {

	private Map<String, HttpSolrClient> clients = new HashMap<>();

	public SolrSearchDao() {
		final Configuration conf = Play.application().configuration();
		final String torrentUrl = conf.getString("torrent.solr.url");

		clients.put(Constants.TORRENT_CONTEXT, new HttpSolrClient(torrentUrl));
	}

	public SolrDocumentList search(final String core, final Map<String, String> criteria) throws CoreNotFoundException, SolrServerException {

		if (!clients.containsKey(core)) {
			throw new CoreNotFoundException(core);
		}

		SolrClient client = clients.get(core);

		List<String> q = new ArrayList<>();
		for (Entry<String, String> entry : criteria.entrySet()) {
			q.add(entry.getKey() + ":" + entry.getValue());
		}

		SolrQuery query = new SolrQuery(StringUtils.join(q.toArray(), " AND "));
		QueryResponse res = client.query(query);

		return res.getResults();
	}
}