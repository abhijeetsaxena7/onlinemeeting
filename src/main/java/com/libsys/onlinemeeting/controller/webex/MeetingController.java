package com.libsys.onlinemeeting.controller.webex;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;
import com.libsys.onlinemeeting.config.vendor.webex.WebexConfiguration;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Meeting;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Spark;
import com.libsys.onlinemeeting.model.OnlineMeetingModel;

/**
 * @author Abhijeet Saxena
 * Class containing meeting related operations.
 */
@RestController("Webex_MeetingController")
@RequestMapping(Constants.VendorPath.WEBEX + "/meeting")
public class MeetingController {
	private WebexConfiguration webexConfig;
	private Webex webex;
	
	@Autowired
	public MeetingController(WebexConfiguration webexConfig, Webex webex) {
		this.webexConfig = webexConfig;
		this.webex = webex;
	}
	
	/**
	 * Create a meeting in webex teams.
	 * Get access token from session.
	 * Build spark object from token.
	 * Set meeting object and call request. 
	 * @param request
	 * @param meetingModel
	 * @return If success, httpstatus 201 and onlineMeetingModel , else httpstatus 500 and error msg 
	 */
	@PostMapping("")
	public ResponseEntity createMeeting(HttpServletRequest request, @RequestBody OnlineMeetingModel meetingModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			
			Meeting meeting = new Meeting();
			meeting.setTitle(meetingModel.getSubject());
			meeting.setStart(meetingModel.getStartDatetime().getTime());
			meeting.setEnd(meetingModel.getEndDatetime().getTime());			

			Meeting res = spark.meeting().post(meeting);
			meetingModel.setObjectId(res.getId());
			meetingModel.setJoinWebUrl(res.getWebLink());
			resEntity = new ResponseEntity(meetingModel,HttpStatus.CREATED);
		}catch(Throwable e) {
			e.printStackTrace(); 
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
		return resEntity;
	} 
	
	/**
	 * Update meeting details in webex meeting
	 * Get access token from session.
	 * Build spark object from token.
	 * Set meeting object and call request. 
	 * @param request
	 * @param meetingModel
	 * @return If success, httpstatus 201 and onlineMeetingModel , else httpstatus 500 and error msg
	 */
	@PutMapping("")
	public ResponseEntity updateMeeting(HttpServletRequest request, @RequestBody OnlineMeetingModel meetingModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			
			Meeting meeting = new Meeting();
			meeting.setTitle(meetingModel.getSubject());
			meeting.setStart(meetingModel.getStartDatetime().getTime());
			meeting.setEnd(meetingModel.getEndDatetime().getTime());			

			Meeting res = spark.meeting().path("/"+meeting.getId()).put(meeting);
			resEntity = new ResponseEntity(meetingModel,HttpStatus.CREATED);
		}catch(Throwable e) {
			e.printStackTrace(); 
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
		return resEntity;
	}
	
	/**
	 * Delete meeting in webex teams
	 * Get access token from session.
	 * Build spark object from token.
	 * Set meeting Id and call request. 
	 * @param request
	 * @param meetingId
	 * @return
	 */
	@DeleteMapping("")
	public ResponseEntity deleteMeeting(HttpServletRequest request, @RequestParam String meetingId) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			spark.meeting().path("/"+meetingId).delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

}
