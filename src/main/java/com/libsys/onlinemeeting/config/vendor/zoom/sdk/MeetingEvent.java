package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

public class MeetingEvent {
	String event;
	MeetingUser payload;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public MeetingUser getPayload() {
		return payload;
	}
	public void setPayload(MeetingUser payload) {
		this.payload = payload;
	}
}
