package com.libsys.onlinemeeting.controller.microsoft;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.GraphServiceClientWrapper;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftScopes;
import com.libsys.onlinemeeting.model.OnlineMeetingModel;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.extensions.OnlineMeeting;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.extensions.IOnlineMeetingCollectionRequest;

/**
 * 
 * @author Abhijeet Saxena
 * Class contains methods related to meeting operation in microsoft. * 
 */
@RestController("Microsoft_MeetingController")
@RequestMapping(Constants.VendorPath.MICROSOFT + "/meeting")
public class MeetingController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;
	@Autowired
	private SessionManagementHelper sessionManagementHelper;
	
	/**
	 * 
	 * Create a meeting with given subject, start date and end date.
	 * Meeting is accessed in MsTeams.
	 * Get IAuthenticationResult object from session.
	 * Set access token in header.
	 * Build request and get response.
	 * 	  
	 * @param request
	 * @param response
	 * @param onlineMeetingModel
	 * @return If success, HttpStatus 204 and onlineMeetingModel, else HttpStatus 500 and error msg
	 */
	@PostMapping("")
	public ResponseEntity createMeeting(HttpServletRequest request, HttpServletResponse response, @RequestBody OnlineMeetingModel onlineMeetingModel){
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Meeting.Create.values()));
//			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getSessionPrincipal(request);
			String accessToken = microsoft.getAccessTokenFromSession(request);

			OnlineMeeting meeting = new OnlineMeeting();
			meeting.subject = onlineMeetingModel.getSubject();
			meeting.startDateTime = onlineMeetingModel.getStartDatetime();
			meeting.endDateTime = onlineMeetingModel.getEndDatetime();
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + accessToken);
			
			IOnlineMeetingCollectionRequest req = graphClientWrapper.getGraphServiceClient().me().onlineMeetings().buildRequest(Arrays.asList(option));
			OnlineMeeting res = req.post(meeting);		
			onlineMeetingModel.setObjectId(res.id);
			onlineMeetingModel.setJoinWebUrl(res.joinWebUrl);
			resEntity = new ResponseEntity<>(onlineMeetingModel,HttpStatus.CREATED);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
	/**
	 * Deletes a meeting.
	 * 
	 * @param request
	 * @param response
	 * @param meetingId
	 * @return If success, HttpStatus 204, Else HttpStatus 500
	 */
	@DeleteMapping("")
	public ResponseEntity deleteMeeting(HttpServletRequest request, HttpServletResponse response, @RequestParam String meetingId){
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Meeting.Delete.values()));
//			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getSessionPrincipal(request);			
			String accessToken = microsoft.getAccessTokenFromSession(request);

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + accessToken);
			
			graphClientWrapper.getGraphServiceClient().me().onlineMeetings(meetingId).buildRequest(Arrays.asList(option)).delete();			
			resEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}

}
