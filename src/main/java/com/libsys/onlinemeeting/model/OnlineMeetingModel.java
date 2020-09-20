package com.libsys.onlinemeeting.model;

import java.io.Serializable;
import java.util.Calendar;

public class OnlineMeetingModel implements Serializable{
	private String objectId;
	private String subject;
	private Calendar startDatetime;
	private Calendar endDatetime;
	private String joinWebUrl;
	//zoom related fields
	private String password;
	private String startUrl;
	private String description;
	
	public OnlineMeetingModel() {
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Calendar getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(Calendar startDatetime) {
		this.startDatetime = startDatetime;
	}

	public Calendar getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(Calendar endDatetime) {
		this.endDatetime = endDatetime;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getJoinWebUrl() {
		return joinWebUrl;
	}

	public void setJoinWebUrl(String joinWebUrl) {
		this.joinWebUrl = joinWebUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
