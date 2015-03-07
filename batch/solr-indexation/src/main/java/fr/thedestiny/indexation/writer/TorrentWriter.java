package fr.thedestiny.indexation.writer;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;

import fr.thedestiny.indexation.util.SolrConnection;

public class TorrentWriter implements ItemWriter<Map<String, Object>> {

	private static final Logger LOGGER = Logger.getLogger(TorrentWriter.class);

	private final SolrConnection solrConnection;

	public TorrentWriter(final SolrConnection solrConnection) {
		this.solrConnection = solrConnection;
	}

	@Override
	public void write(List<? extends Map<String, Object>> items) throws Exception {

		LOGGER.debug(items);

		solrConnection.addDocuments(items);
	}

}
