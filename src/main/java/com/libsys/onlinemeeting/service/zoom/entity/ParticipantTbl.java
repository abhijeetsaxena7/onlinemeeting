package com.libsys.onlinemeeting.service.zoom.entity;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@NamedQuery(name = "ParticipantTbl.getMaxInstanceId",query = "select max(p.participantPk.instanceId) from ParticipantTbl p where p.participantPk.meetingId=:meetingId and p.participantPk.participantId=:participantId")
@Entity
public class ParticipantTbl {
	@EmbeddedId
	private ParticipantPk participantPk;
	
	private String userId;
	private Date joinTime;
	private Date leaveTime;

	public ParticipantPk getParticipantPk() {
		return participantPk;
	}
	public void setParticipantPk(ParticipantPk participantPk) {
		this.participantPk = participantPk;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}
	public Date getLeaveTime() {
		return leaveTime;
	}
	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}
}
