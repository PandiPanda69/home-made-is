package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;

public class AbstractService {

	private String persistenceContext;

	protected AbstractService() {
		this.persistenceContext = "default";
	}

	protected AbstractService(final String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	protected <T> T processInTransaction(final InTransactionFunction action) {
		T result = null;

		EntityManager em = JPA.em(persistenceContext);

		em.getTransaction().begin();
		try {
			JPA.bindForCurrentThread(em);
			result = action.doWork(em);
			em.getTransaction().commit();
		} catch (Throwable t) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException(t);
		} finally {
			JPA.bindForCurrentThread(null);
			if (em != null) {
				em.close();
			}
		}

		return result;
	}

	protected void processInTransaction(final InTransactionProcedure action) {

		EntityManager em = JPA.em(persistenceContext);

		em.getTransaction().begin();
		try {
			JPA.bindForCurrentThread(em);
			action.doWork(em);
			em.getTransaction().commit();
		} catch (Throwable t) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException(t);
		} finally {
			JPA.bindForCurrentThread(null);
			if (em != null) {
				em.close();
			}
		}
	}
}
