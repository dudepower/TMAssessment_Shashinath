package com.assessment.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.assessment.access.Task;
import com.assessment.access.TaskAccessBean;
import com.assessment.access.Team;
import com.assessment.access.TeamAccessBean;
import com.assessment.access.TeamSkill;
import com.assessment.access.TeamSkillAccessBean;

public class AsignmentEngine {

	private TaskAccessBean taskAccessBean;

	private TeamAccessBean teamAccessBean;

	private TeamSkillAccessBean teamSkillAccessBean;

	public AsignmentEngine() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("com.assessment.access");
		taskAccessBean = ctx.getBean(TaskAccessBean.class);
		teamAccessBean = ctx.getBean(TeamAccessBean.class);
		teamSkillAccessBean = ctx.getBean(TeamSkillAccessBean.class);
		List<Task> taskList = taskAccessBean.findAll();
		List<Team> teamList = teamAccessBean.findAll();
		List<TeamSkill> teamSkillList = teamSkillAccessBean.findAll();
		getTasks(taskList, teamList, teamSkillList);
	}

	private void getTasks(List<Task> taskList, List<Team> teamList, List<TeamSkill> teamSkillList) {
		Map<Task, TeamSkill> map = new HashMap<>();
		List<TeamSkill> tsList = null;
		String skill = "";
		int count = 0;
		int modulo = 0;
		for (Task task : taskList) {
			if (!skill.equals(task.getSkill())) {
				skill = task.getSkill();
				count = 0;
				tsList = teamSkillAccessBean.findBySkill(skill);
				modulo = tsList.size();
			}

			map.put(task, tsList.get(count % modulo));
		}

		for (Entry<Task, TeamSkill> entry : map.entrySet()) {
			System.out.println("Task:" + entry.getKey().getTaskId() + "\tTeam:" + entry.getValue().getTeamId());
		}
	}

}
