package com.libsys.onlinemeeting.model;

import java.io.Serializable;
import java.util.Date;

public class EventModel implements Serializable{
	private String id;
	private String subject;
	private String body;
	private boolean isHtml;
	private Date startDatetime;
	private Date endDatetime;
	private boolean isOnlineMeeting;
	private String onlineMeetingUrl;
	private boolean setReminder;

	//for LSAC to get info back
	private Integer moduleType;
	private Integer moduleSubType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public boolean isHtml() {
		return isHtml;
	}
	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
	public Date getStartDatetime() {
		return startDatetime;
	}
	public void setStartDatetime(Date startDatetime) {
		this.startDatetime = startDatetime;
	}
	public Date getEndDatetime() {
		return endDatetime;
	}
	public void setEndDatetime(Date endDatetime) {
		this.endDatetime = endDatetime;
	}
	public boolean isOnlineMeeting() {
		return isOnlineMeeting;
	}
	public void setOnlineMeeting(boolean isOnlineMeeting) {
		this.isOnlineMeeting = isOnlineMeeting;
	}
	public String getOnlineMeetingUrl() {
		return onlineMeetingUrl;
	}
	public void setOnlineMeetingUrl(String onlineMeetingUrl) {
		this.onlineMeetingUrl = onlineMeetingUrl;
	}
	public boolean isSetReminder() {
		return setReminder;
	}
	public void setSetReminder(boolean setReminder) {
		this.setReminder = setReminder;
	}
	public Integer getModuleType() {
		return moduleType;
	}
	public void setModuleType(Integer moduleType) {
		this.moduleType = moduleType;
	}
	public Integer getModuleSubType() {
		return moduleSubType;
	}
	public void setModuleSubType(Integer moduleSubType) {
		this.moduleSubType = moduleSubType;
	}
}
