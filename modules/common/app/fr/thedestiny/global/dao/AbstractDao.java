package fr.thedestiny.global.dao;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;

public abstract class AbstractDao<T extends Object> {

	protected String persistenceContext = null;

	protected AbstractDao(final String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	private T save(T obj) {
		return em().merge(obj);
	}

	public T save(EntityManager em, T obj) {
		if (em == null) {
			return save(obj);
		}

		return em.merge(obj);
	}

	public void persist(EntityManager em, T obj) {
		if (em == null) {
			em().persist(obj);
		}

		em.persist(obj);
	}

	protected final T findById(EntityManager em, Integer id, Class<T> clazz) {
		if (em == null) {
			em = em();
		}

		return em.find(clazz, id);
	}

	protected EntityManager em() {
		return JPA.em(persistenceContext);
	}
}
