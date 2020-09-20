package com.libsys.onlinemeeting.model;

import java.io.Serializable;
import java.util.Date;

public class MeetingEventModel implements Serializable{
	String meetingId;
	Date endTime;
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}