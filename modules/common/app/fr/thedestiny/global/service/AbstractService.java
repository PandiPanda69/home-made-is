package fr.thedestiny.global.service;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;

public class AbstractService {

	private String persistenceContext;

	protected AbstractService() {
		this.persistenceContext = "default";
	}

	protected AbstractService(String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	protected <T> T processInTransaction(InTransactionAction action) throws Exception {
		T result = null;

		EntityManager em = JPA.em(persistenceContext);

		em.getTransaction().begin();
		try {
			JPA.bindForCurrentThread(em);
			result = action.doWork(em);
			em.getTransaction().commit();
		} catch (Exception ex) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw ex;
		} finally {
			JPA.bindForCurrentThread(null);
			if (em != null) {
				em.close();
			}
		}

		return result;
	}
}
