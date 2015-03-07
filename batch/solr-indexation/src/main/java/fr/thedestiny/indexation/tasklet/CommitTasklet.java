package fr.thedestiny.indexation.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import fr.thedestiny.indexation.util.SolrConnection;

public class CommitTasklet implements Tasklet {

	private final SolrConnection solrConnection;

	public CommitTasklet(final SolrConnection solrConnection) {
		this.solrConnection = solrConnection;
	}

	@Override
	public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {

		solrConnection.commit();
		return RepeatStatus.FINISHED;
	}

}
