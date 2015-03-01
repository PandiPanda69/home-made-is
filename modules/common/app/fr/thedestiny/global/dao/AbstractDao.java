package fr.thedestiny.global.dao;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;

public abstract class AbstractDao<T extends Object> {

	protected String persistenceContext = null;

	public AbstractDao(final String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	private T save(T obj) {
		return JPA.em(persistenceContext).merge(obj);
	}

	public T save(EntityManager em, T obj) {
		if (em == null) {
			return save(obj);
		}

		return em.merge(obj);
	}

	protected final T findById(EntityManager em, Integer id, Class<T> clazz) {
		if (em == null) {
			em = JPA.em(persistenceContext);
		}

		return em.find(clazz, id);
	}
}
