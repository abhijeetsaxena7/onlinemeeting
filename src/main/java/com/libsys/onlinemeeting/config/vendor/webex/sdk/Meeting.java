package com.libsys.onlinemeeting.config.vendor.webex.sdk;

import java.util.Date;

public class Meeting {
	String id;
	String title;
	String agenda;
	String password;
	Date start;
	Date end;
	String timezone;
	boolean enableAutoRecordMeeting = false;
	boolean allowAnyUserToBeCoHost = false;
	Person[] invitees;
	String webLink;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAgenda() {
		return agenda;
	}
	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public boolean isEnableAutoRecordMeeting() {
		return enableAutoRecordMeeting;
	}
	public void setEnableAutoRecordMeeting(boolean enableAutoRecordMeeting) {
		this.enableAutoRecordMeeting = enableAutoRecordMeeting;
	}
	public boolean isAllowAnyUserToBeCoHost() {
		return allowAnyUserToBeCoHost;
	}
	public void setAllowAnyUserToBeCoHost(boolean allowAnyUserToBeCoHost) {
		this.allowAnyUserToBeCoHost = allowAnyUserToBeCoHost;
	}
	public Person[] getInvitees() {
		return invitees;
	}
	public void setInvitees(Person[] invitees) {
		this.invitees = invitees;
	}
	public String getWebLink() {
		return webLink;
	}
	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}

	

}
