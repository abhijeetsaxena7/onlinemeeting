package com.libsys.onlinemeeting.service.zoom.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepo extends CrudRepository<ParticipantTbl, ParticipantPk> {

	Integer getMaxInstanceId(String meetingId, String participantId);

	Optional<List<ParticipantTbl>> findByParticipantPkMeetingId(String meetingId);

	void deleteByParticipantPkMeetingId(String meetingId);

}
