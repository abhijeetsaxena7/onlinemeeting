package com.libsys.onlinemeeting.controller.zoom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.zoom.AccessToken;
import com.libsys.onlinemeeting.config.vendor.zoom.ZoomConfiguration;
import com.libsys.onlinemeeting.config.vendor.zoom.ZoomConstants;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ParticipantEvent;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.Meeting;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.MeetingEvent;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.MeetingSetting;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.User;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.UserCreate;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ZoomClient;
import com.libsys.onlinemeeting.model.MeetingEventModel;
import com.libsys.onlinemeeting.model.OnlineMeetingModel;
import com.libsys.onlinemeeting.model.ParticipantEventModel;
import com.libsys.onlinemeeting.model.ParticipantInfoModel;
import com.libsys.onlinemeeting.model.UserModel;
import com.libsys.onlinemeeting.service.zoom.MeetingService;
import com.libsys.onlinemeeting.service.zoom.entity.ParticipantTbl;

@RestController("Zoom_MeetingController")
@RequestMapping(Constants.VendorPath.ZOOM + "/meeting")
public class MeetingController {

	@Autowired
	private ZoomConfiguration zoomConfiguration;
	@Autowired
	private MeetingService meetingService;

	@PostMapping("")
	public ResponseEntity createMeeting(HttpServletRequest request,
			@RequestBody OnlineMeetingModel onlineMeetingModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = ((AccessToken) request.getSession().getAttribute("principal")).getAccessToken();

			Meeting meeting = new Meeting();
			meeting.setPassword(onlineMeetingModel.getPassword());
			meeting.setStartTime(onlineMeetingModel.getStartDatetime().getTime());
			meeting.setMeetingType(ZoomConstants.MEETING_TYPE.SCHEDULED);
			meeting.setDuration(
					(int) TimeUnit.MILLISECONDS.toMinutes(onlineMeetingModel.getEndDatetime().getTimeInMillis()
							- onlineMeetingModel.getStartDatetime().getTimeInMillis()));
			meeting.setTopic(onlineMeetingModel.getSubject());
			meeting.setAgenda(onlineMeetingModel.getDescription());
			meeting.setSetting(new MeetingSetting());
			
			ZoomClient zoomClient = ZoomClient.builder(accessToken).build();
			Meeting res = zoomClient.meeting("me").post(meeting);
			onlineMeetingModel.setObjectId(res.getId());
			onlineMeetingModel.setJoinWebUrl(res.getJoinUrl());
			onlineMeetingModel.setStartUrl(res.getStartUrl());
			resEntity = new ResponseEntity(onlineMeetingModel, HttpStatus.CREATED);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	@PatchMapping("")
	public ResponseEntity updateMeeting(HttpServletRequest request,
			@RequestBody OnlineMeetingModel onlineMeetingModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = ((AccessToken) request.getSession().getAttribute("principal")).getAccessToken();

			Meeting meeting = new Meeting();
			if(onlineMeetingModel.getPassword()!=null) {
			meeting.setPassword(onlineMeetingModel.getPassword());
			}
			if(onlineMeetingModel.getStartDatetime()!=null) {
				meeting.setStartTime(onlineMeetingModel.getStartDatetime().getTime());
			}
			
			if(onlineMeetingModel.getEndDatetime()!=null) {
			meeting.setDuration(
					(int) TimeUnit.MILLISECONDS.toMinutes(onlineMeetingModel.getEndDatetime().getTimeInMillis()
							- onlineMeetingModel.getStartDatetime().getTimeInMillis()));
			}
			
			if(onlineMeetingModel.getSubject()!=null) {
				meeting.setTopic(onlineMeetingModel.getSubject());
			}
			
			if(onlineMeetingModel.getDescription()!=null) {
				meeting.setAgenda(onlineMeetingModel.getDescription());
			}
			ZoomClient zoomClient = ZoomClient.builder(accessToken).build();
			
			Meeting res = zoomClient.meeting().path(onlineMeetingModel.getObjectId()).patch(meeting);
			onlineMeetingModel.setObjectId(res.getId());
			onlineMeetingModel.setJoinWebUrl(res.getJoinUrl());
			onlineMeetingModel.setStartUrl(res.getStartUrl());
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	@DeleteMapping("")
	public ResponseEntity deleteMeeting(HttpServletRequest request,@RequestParam String meetingId) {
		ResponseEntity resEntity;
		try {
			String accessToken = ((AccessToken) request.getSession().getAttribute("principal")).getAccessToken();
			ZoomClient zoomClient = ZoomClient.builder(accessToken).build();
			
			zoomClient.meeting().path(meetingId).delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	//for zoom to recieve notification for pariticipant	
	@PostMapping("/event/participant")
	public ResponseEntity getEvent(@RequestBody ParticipantEvent event) {
		try {
			meetingService.persistParticipantDetails(event);
			return new ResponseEntity(HttpStatus.OK);
		}catch (Throwable e) {
			 e.printStackTrace();
			 return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//for zoom to recieve meeting notification
	@PostMapping("/event/meeting")
	public ResponseEntity getEvent(@RequestBody MeetingEvent event) {
		
		List<ParticipantTbl> participants = meetingService.getParticipantsInfoForMeeting(event.getPayload().getMeeting().getId());
		
		Map<String,List<ParticipantTbl>> participantMap = participants.stream().filter(e->e.getUserId()!=null).collect(Collectors.groupingBy(ParticipantTbl::getUserId));
		
		List<ParticipantInfoModel> piModels = new ArrayList<ParticipantInfoModel>();
		
		for(Entry<String, List<ParticipantTbl>>entry:participantMap.entrySet()) {
			ParticipantInfoModel piModel = new ParticipantInfoModel();
			piModel.setMeetingId(event.getPayload().getMeeting().getId());
			piModel.setUserId(entry.getKey());
			piModel.setAttendanceDuration(0);
						
			List<ParticipantTbl> sortedDates = entry.getValue().stream().sorted((o1,o2)->{
				if(o1.getJoinTime()!=null && o2.getJoinTime()!=null) {
					return o1.getJoinTime().compareTo(o2.getJoinTime());
				}
				
				if(o1.getJoinTime()!=null && o2.getJoinTime()==null) {
					return o1.getJoinTime().compareTo(o2.getLeaveTime());
				}
				if(o1.getJoinTime()==null && o2.getJoinTime()!=null) {
					return o1.getLeaveTime().compareTo(o2.getJoinTime());
				}
				
				return o1.getLeaveTime().compareTo(o2.getLeaveTime());
			}).collect(Collectors.toList());
			
			long totalDuration = 0;
			Date leave;
			Date join;
			if(sortedDates.size()<=1) {
				
			}else {
				int len = sortedDates.size();
				if(len%2!=0) {
					if(sortedDates.get(len-1).getJoinTime()!=null) {
						piModel.setRemainder(sortedDates.get(len-1).getJoinTime());
					}else {
						piModel.setRemainder(sortedDates.get(len-1).getLeaveTime());
					}
					len = len-1;
				}
				for(int i=0;i<len-1;i=i+2) {
					if(sortedDates.get(i).getJoinTime()==null) {
						join = sortedDates.get(i).getLeaveTime();
					}else {
						join = sortedDates.get(i).getJoinTime();
					}
					
					if(sortedDates.get(i+1).getJoinTime()==null) {
						leave = sortedDates.get(i+1).getLeaveTime();
					}else {
						leave = sortedDates.get(i+1).getJoinTime();
					}
					
					totalDuration += leave.getTime() - join.getTime();
				}
				piModel.setAttendanceDuration(totalDuration);
			}
			
			piModels.add(piModel);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, Constants.HeaderValue.APPLICATION_JSON);
		
		HttpEntity entity = new HttpEntity(piModels, headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity resEntity = restTemplate.exchange(zoomConfiguration.getAttendanceResponseUrl(), HttpMethod.POST, entity, String.class);
		return new ResponseEntity(HttpStatus.OK);
	}	
}
