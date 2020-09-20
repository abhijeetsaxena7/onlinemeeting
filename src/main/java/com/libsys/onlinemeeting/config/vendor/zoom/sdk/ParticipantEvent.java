package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

public class ParticipantEvent {
	String event;
	Meeting payload;
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public Meeting getPayload() {
		return payload;
	}
	public void setPayload(Meeting payload) {
		this.payload = payload;
	}
}
