package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

public class MeetingUser {
	String accountId;
	Meeting meeting;
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public Meeting getMeeting() {
		return meeting;
	}
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
}
