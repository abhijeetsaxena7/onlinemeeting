package com.libsys.onlinemeeting.config.vendor.zoom;

public interface ZoomConstants {

	interface MEETING_TYPE{
		int SCHEDULED = 2;
	}
	
	interface USER_TYPE{
		int BASIC = 1;
		int LICENSED = 2;
		int ON_PREM = 3;
	}
	
	interface EVENT_TYPE{
		String PARTICIPANT_JOINED = "";
		String PARTICIPANT_LEFT = "";
		String MEETING_ENDED = "";
	}
}
