package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

public abstract class InTransactionProcedure {

	public abstract void doWork(EntityManager em) throws Exception;
}
