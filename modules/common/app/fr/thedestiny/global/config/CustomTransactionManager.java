package fr.thedestiny.global.config;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class CustomTransactionManager extends JpaTransactionManager {

	private static final long serialVersionUID = 8953863845586094286L;

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		System.out.println("Begin transaction");
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		System.out.println("Commit transaction");
	}
}
