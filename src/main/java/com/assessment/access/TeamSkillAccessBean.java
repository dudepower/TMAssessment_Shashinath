package com.assessment.access;

import java.util.List;

import javax.persistence.Entity;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.REQUIRED)
@SuppressWarnings("unchecked")
public class TeamSkillAccessBean extends BaseDataAccessBean {

	
	public List<TeamSkill> findById(String id) {
		return em.createQuery("SELECT o FROM TeamSkill o where teamId=" + id).getResultList();
	}

	public List<TeamSkill> findBySkill(String skill) {
		return em.createQuery("SELECT o FROM TeamSkill o where skill=" + skill).getResultList();
	}
	
	public List<TeamSkill> findAll() {
		return em.createQuery("SELECT o FROM TeamSkill o order by skill").getResultList();
	}
}