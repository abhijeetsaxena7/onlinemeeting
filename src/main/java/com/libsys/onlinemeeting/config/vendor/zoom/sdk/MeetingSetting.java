package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

public class MeetingSetting {
	boolean hostVideo = true;
	boolean participantVideo = false;
	boolean cnMeeting = false;
	boolean inMeeting = true;
	boolean joinBeforeHost = true;
	boolean muteUponEntry = false;
	String autoRecording = "none";
    boolean waitingRoom = true;
    boolean meetingAuthentication = true;
        
	public boolean getHostVideo() {
		return hostVideo;
	}
	public void setHostVideo(boolean hostVideo) {
		this.hostVideo = hostVideo;
	}
	public boolean getParticipantVideo() {
		return participantVideo;
	}
	public void setParticipantVideo(boolean participantVideo) {
		this.participantVideo = participantVideo;
	}
	public boolean getCnMeeting() {
		return cnMeeting;
	}
	public void setCnMeeting(boolean cnMeeting) {
		this.cnMeeting = cnMeeting;
	}
	public boolean getInMeeting() {
		return inMeeting;
	}
	public void setInMeeting(boolean inMeeting) {
		this.inMeeting = inMeeting;
	}
	public boolean getJoinBeforeHost() {
		return joinBeforeHost;
	}
	public void setJoinBeforeHost(boolean joinBeforeHost) {
		this.joinBeforeHost = joinBeforeHost;
	}
	public boolean getMuteUponEntry() {
		return muteUponEntry;
	}
	public void setMuteUponEntry(boolean muteUponEntry) {
		this.muteUponEntry = muteUponEntry;
	}
	public String getAutoRecording() {
		return autoRecording;
	}
	public void setAutoRecording(String autoRecording) {
		this.autoRecording = autoRecording;
	}
	public boolean getWaitingRoom() {
		return waitingRoom;
	}
	public void setWaitingRoom(boolean waitingRoom) {
		this.waitingRoom = waitingRoom;
	}
	public boolean getMeetingAuthentication() {
		return meetingAuthentication;
	}
	public void setMeetingAuthentication(boolean meetingAuthentication) {
		this.meetingAuthentication = meetingAuthentication;
	}
    
    
    
}
