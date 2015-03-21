package fr.thedestiny.indexation.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrConnection {

	private final SolrClient client;

	private SolrConnection(final String url, final String core) {

		String coreUrl;

		if (url.endsWith("/")) {
			coreUrl = url + core;
		} else {
			coreUrl = url + '/' + core;
		}

		client = new HttpSolrClient(coreUrl);
	}

	public UpdateResponse addDocuments(final List<? extends Map<String, Object>> documents) throws SolrServerException, IOException {

		Collection<SolrInputDocument> documentsToAdd = new ArrayList<>();

		for (Map<String, Object> current : documents) {
			SolrInputDocument document = new SolrInputDocument();

			for (Entry<String, Object> entry : current.entrySet()) {
				document.addField(entry.getKey(), entry.getValue());
			}

			documentsToAdd.add(document);
		}

		return client.add(documentsToAdd);
	}

	public void commit() throws SolrServerException, IOException {
		client.commit();
	}
}
