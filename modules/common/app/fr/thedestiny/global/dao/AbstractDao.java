package fr.thedestiny.global.dao;

import java.util.List;

import javax.persistence.EntityManager;

import play.db.jpa.JPA;

public abstract class AbstractDao<T extends Object> {

	private Class<T> clazz;
	protected String persistenceContext = null;

	protected AbstractDao(final String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	protected AbstractDao(final String persistenceContext, final Class<T> clazz) {
		this(persistenceContext);
		this.clazz = clazz;
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
			return;
		}

		em.persist(obj);
	}

	public List<T> findAll() {
		return em().createQuery("from " + clazz.getName(), clazz).getResultList();
	}

	public final T findById(EntityManager em, Integer id) {
		if (em == null) {
			em = em();
		}

		return em.find(clazz, id);
	}

	protected EntityManager em() {
		return JPA.em(persistenceContext);
	}
}
