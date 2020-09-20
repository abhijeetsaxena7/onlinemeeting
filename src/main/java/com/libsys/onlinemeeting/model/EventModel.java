package com.libsys.onlinemeeting.model;

import java.io.Serializable;

public class EventModel implements Serializable{
	String eventType;
	Object data;
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
}
