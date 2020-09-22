package com.libsys.onlinemeeting.model;

import java.io.Serializable;
import java.util.Date;

public class ParticipantInfoModel implements Serializable{
	String userId;
	String meetingId;
	long attendanceDuration;
	Date remainder;		//incases where just a single event is logged like meeting joined or meeting left is only logged.
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public long getAttendanceDuration() {
		return attendanceDuration;
	}
	public void setAttendanceDuration(long attendanceDuration) {
		this.attendanceDuration = attendanceDuration;
	}
	public Date getRemainder() {
		return remainder;
	}
	public void setRemainder(Date remainder) {
		this.remainder = remainder;
	}
	
}
