package fr.thedestiny.global.config;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import play.Logger;

public class CustomTransactionManager extends JpaTransactionManager {

	private static final long serialVersionUID = 8953863845586094286L;

	@Override
	protected void doBegin(final Object transaction, final TransactionDefinition definition) {
		Logger.debug("Begin transaction");
		super.doBegin(transaction, definition);
	}

	@Override
	protected void doCommit(final DefaultTransactionStatus status) {
		Logger.debug("Commit transaction");
		super.doCommit(status);
	}

	@Override
	protected void doRollback(final DefaultTransactionStatus status) {
		Logger.debug("Rollback transaction");
		super.doRollback(status);
	}
}
