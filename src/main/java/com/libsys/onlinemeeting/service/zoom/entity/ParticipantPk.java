package com.libsys.onlinemeeting.service.zoom.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ParticipantPk implements Serializable{
	public String meetingId;
	public String participantId;
	public int instanceId;
	
	public ParticipantPk() {
	}
	
	public ParticipantPk(String meetingId, String participantId, int instanceId) {
		this.meetingId = meetingId;
		this.participantId = participantId;
		this.instanceId = instanceId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getParticipantId() {
		return participantId;
	}
	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + instanceId;
		result = prime * result + ((meetingId == null) ? 0 : meetingId.hashCode());
		result = prime * result + ((participantId == null) ? 0 : participantId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipantPk other = (ParticipantPk) obj;
		if (instanceId != other.instanceId)
			return false;
		if (meetingId == null) {
			if (other.meetingId != null)
				return false;
		} else if (!meetingId.equals(other.meetingId))
			return false;
		if (participantId == null) {
			if (other.participantId != null)
				return false;
		} else if (!participantId.equals(other.participantId))
			return false;
		return true;
	}
}
