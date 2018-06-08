package com.assessment.access;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class BaseDataAccessBean {

	@PersistenceContext
	protected EntityManager em;

	public void save(Object entity) {
		em.merge(entity);
	}

	public void update(Object entity) {
		em.merge(entity);
	}

	public void delete(Object entity) {
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

}