package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

public abstract class InTransactionFunction<T> {

	public abstract T doWork(EntityManager em) throws Exception;
}
