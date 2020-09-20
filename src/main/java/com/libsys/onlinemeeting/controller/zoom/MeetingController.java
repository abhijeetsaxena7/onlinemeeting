package com.libsys.onlinemeeting.controller.zoom;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import com.libsys.onlinemeeting.model.EventModel;
import com.libsys.onlinemeeting.model.MeetingEventModel;
import com.libsys.onlinemeeting.model.OnlineMeetingModel;
import com.libsys.onlinemeeting.model.ParticipantEventModel;
import com.libsys.onlinemeeting.model.UserModel;

@RestController("Zoom_MeetingController")
@RequestMapping(Constants.VendorPath.ZOOM + "/meeting")
public class MeetingController {

	@Autowired
	private ZoomConfiguration zoomConfiguration;

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
	
	@PostMapping("/event/participant")
	public ResponseEntity getEvent(@RequestBody ParticipantEvent event) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(()->{
			sendParticipantEventToApi(event);
		});
		return new ResponseEntity(HttpStatus.OK);
	}
	
	public void sendParticipantEventToApi(ParticipantEvent event){
		RestTemplate restTemplate = new RestTemplate();
		
		ParticipantEventModel peModel = new ParticipantEventModel();
		peModel.setMeetingId(event.getPayload().getId());
		peModel.setUserId(event.getPayload().getParticipant().getId());
		peModel.setJoinTime(event.getPayload().getParticipant().getJoinTime());
		peModel.setLeaveTime(event.getPayload().getParticipant().getLeaveTime());
		EventModel eventModel = new EventModel();
		eventModel.setEventType(event.getEvent());
		eventModel.setData(peModel);
		
		HttpEntity entity = new HttpEntity(eventModel);
		restTemplate.exchange(zoomConfiguration.getEventResponseUrl(), HttpMethod.POST, entity, String.class);
	}
	

	@PostMapping("/event/meeting")
	public ResponseEntity getEvent(@RequestBody MeetingEvent event) {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(()->{
			sendMeetingEventToApi(event);
		});
		return new ResponseEntity(HttpStatus.OK);
	}

	private void sendMeetingEventToApi(MeetingEvent event) {
		RestTemplate restTemplate = new RestTemplate();
		
		MeetingEventModel meModel = new MeetingEventModel();
		meModel.setMeetingId(event.getPayload().getMeeting().getId());
		Calendar cal = Calendar.getInstance();
		cal.setTime(event.getPayload().getMeeting().getStartTime());
		cal.add(event.getPayload().getMeeting().getDuration(), Calendar.MINUTE);
		meModel.setEndTime(cal.getTime());
		EventModel eventModel = new EventModel();
		eventModel.setEventType(event.getEvent());
		eventModel.setData(meModel);
		
		HttpEntity entity = new HttpEntity(meModel);
		restTemplate.exchange(zoomConfiguration.getEventResponseUrl(), HttpMethod.POST, entity, String.class);
		
	}
}
