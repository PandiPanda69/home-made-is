package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

public abstract class InTransactionFunction {

	public abstract <T> T doWork(EntityManager em) throws Exception;
}
