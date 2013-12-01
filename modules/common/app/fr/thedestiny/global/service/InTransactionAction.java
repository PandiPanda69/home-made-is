package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

public interface InTransactionAction {

	public <T> T doWork(EntityManager em) throws Exception;
}
