package com.assessment.access;

import java.util.List;

import javax.persistence.Entity;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class TaskAccessBean extends BaseDataAccessBean {

	public Task findById(long id) {
		return (Task) em.createQuery("SELECT o FROM Task o where taskId=" + id).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Task> findAll() {
		return em.createQuery("SELECT o FROM Task o order by skill").getResultList();
	}
}