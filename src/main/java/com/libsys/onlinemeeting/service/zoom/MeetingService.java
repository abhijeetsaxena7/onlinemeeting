package com.libsys.onlinemeeting.service.zoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ParticipantEvent;
import com.libsys.onlinemeeting.service.zoom.entity.ParticipantPk;
import com.libsys.onlinemeeting.service.zoom.entity.ParticipantRepo;
import com.libsys.onlinemeeting.service.zoom.entity.ParticipantTbl;

@Service
public class MeetingService {
	@Autowired
	private ParticipantRepo participantRepo;
	
	public ParticipantTbl persistParticipantDetails(ParticipantEvent event) {
		String meetingId = event.getPayload().getId();
		String participantId = event.getPayload().getParticipant().getUserId();
		
		ParticipantTbl participantTbl = new ParticipantTbl();
		
		Integer maxInstanceId = participantRepo.getMaxInstanceId(meetingId,participantId);
		if(maxInstanceId==null) {
			maxInstanceId=0;
		}
		participantTbl.setParticipantPk(new ParticipantPk(meetingId, participantId, maxInstanceId+1));
		participantTbl.setUserId(event.getPayload().getParticipant().getId());
		if(event.getPayload().getParticipant().getJoinTime()!=null) {
			participantTbl.setJoinTime(event.getPayload().getParticipant().getJoinTime());
		}
		if(event.getPayload().getParticipant().getJoinTime()!=null) {
			participantTbl.setLeaveTime(event.getPayload().getParticipant().getLeaveTime());
		}
		
		participantRepo.save(participantTbl);
		return participantTbl;
	}
	
	public List<ParticipantTbl> getParticipantsInfoForMeeting(String meetingId){
		Optional<List<ParticipantTbl>> data= participantRepo.findByParticipantPkMeetingId(meetingId);
		
		return data.orElse(new ArrayList<ParticipantTbl>());
	}
	
	@Transactional
	public void deleteParticipants(String meetingId) {
		participantRepo.deleteByParticipantPkMeetingId(meetingId);
	}

}
