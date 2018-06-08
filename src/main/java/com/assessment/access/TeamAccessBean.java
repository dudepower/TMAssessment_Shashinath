package com.assessment.access;

import java.util.List;

import javax.persistence.Entity;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class TeamAccessBean extends BaseDataAccessBean {

	public Team findById(long id) {
		return (Team) em.createQuery("SELECT o FROM Team o where teamId=" + id).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Team> findAll() {
		return em.createQuery("SELECT o FROM Team o").getResultList();
	}
}