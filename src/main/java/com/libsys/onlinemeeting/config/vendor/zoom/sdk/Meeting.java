package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import java.util.Date;

public class Meeting {
	String id;
	String assistantId;
	String hostEmail;
	String topic;
	int meetingType;
	Date startTime;
	int duration; 	//in minutes
	Date createdAt;
	String agenda;
	String startUrl;
	String joinUrl;
	String password; //max 10 characters  contain following characters [a-z A-Z 0-9 @-_*]
	int pmi;
	MeetingSetting setting;
	Participant participant;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssistantId() {
		return assistantId;
	}
	public void setAssistantId(String assistantId) {
		this.assistantId = assistantId;
	}
	public String getHostEmail() {
		return hostEmail;
	}
	public void setHostEmail(String hostEmail) {
		this.hostEmail = hostEmail;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public int getMeetingType() {
		return meetingType;
	}
	public void setMeetingType(int meetingType) {
		this.meetingType = meetingType;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getAgenda() {
		return agenda;
	}
	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}
	public String getStartUrl() {
		return startUrl;
	}
	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
	public String getJoinUrl() {
		return joinUrl;
	}
	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getPmi() {
		return pmi;
	}
	public void setPmi(int pmi) {
		this.pmi = pmi;
	}
	public MeetingSetting getSetting() {
		return setting;
	}
	public void setSetting(MeetingSetting setting) {
		this.setting = setting;
	}
	public Participant getParticipant() {
		return participant;
	}
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}
}
