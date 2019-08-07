package com.ks0100.wp.dto;

import java.io.Serializable;

import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;

public class TongJi implements Serializable {

  private static final long serialVersionUID = 6030092720386982613L;

  private int all;//任务总数
  private int over;//已完成的任务个数
  private int noDone;//未完成的任务个数
  private int overdue;//逾期的任务个数
  private String name;//姓名
  private int planDay;//计划工时
  private int realityDay;//实际工时

  public int getPlanDay() {
	return this.planDay;
  }

  public void setPlanDay(int planDay) {
	this.planDay = planDay;
  }

  public int getRealityDay() {
	return this.realityDay;
  }

  public void setRealityDay(int realityDay) {
	this.realityDay = realityDay;
  }

  public int getAll() {
	return this.all;
  }

  public void setAll(int all) {
	this.all = all;
  }

  public int getOver() {
	return this.over;
  }

  public void setOver(int over) {
	this.over = over;
  }

  public int getNoDone() {
	return this.noDone;
  }

  public void setNoDone(int noDone) {
	this.noDone = noDone;
  }

  public int getOverdue() {
	return this.overdue;
  }

  public void setOverdue(int overdue) {
	this.overdue = overdue;
  }

  public String getName() {
	return this.name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public static TongJi parse(User user) {
	TongJi tj = new TongJi();
	int taskCount = user.getTaskCount();
	tj.setName(user.getName());
	tj.setAll(taskCount);

	int overTaskCount = taskCount - user.getNoTaskCount();
	tj.setOver(overTaskCount < 0 ? 0 : overTaskCount);

	tj.setNoDone(user.getNoTaskCount());
	tj.setOverdue(user.getTardyTaskCount());
	tj.setPlanDay(user.getPlanDay());
	tj.setRealityDay(user.getRealityDay());
	return tj;
  }

  public static TongJi parse(Project project) {
	TongJi tj = new TongJi();
	int taskCount = project.getTaskCount();
	tj.setName(project.getName());
	tj.setAll(taskCount);
	int overTaskCount = taskCount - project.getNoTaskCount();
	tj.setOver(overTaskCount < 0 ? 0 : overTaskCount);
	tj.setNoDone(project.getNoTaskCount());
	tj.setOverdue(project.getTardyTaskCount());
	tj.setRealityDay(project.getRealityDay());
	tj.setPlanDay(project.getPlanDay());
	return tj;
  }

  public static TongJi parse(Team team) {
	TongJi tj = new TongJi();
	int taskCount = team.getTaskCount();
	tj.setName(team.getName());
	tj.setAll(taskCount);
	int overTaskCount = taskCount - team.getNoTaskCount();
	tj.setOver(overTaskCount < 0 ? 0 : overTaskCount);
	tj.setNoDone(team.getNoTaskCount());
	tj.setOverdue(team.getTardyTaskCount());
	tj.setRealityDay(team.getRealityDay());
	tj.setPlanDay(team.getPlanDay());
	return tj;
  }
}
