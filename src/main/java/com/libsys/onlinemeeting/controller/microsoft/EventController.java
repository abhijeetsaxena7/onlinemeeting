package com.libsys.onlinemeeting.controller.microsoft;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.GraphServiceClientWrapper;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.OnlineMeetingInfo;
import com.microsoft.graph.models.generated.OnlineMeetingProviderType;

@RestController("Microsoft_EventController")
@RequestMapping(Constants.VendorPath.MICROSOFT + "/event")
public class EventController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;
	@Autowired
	private SessionManagementHelper sessionManagementHelper;

	public ResponseEntity createEvent(HttpServletRequest request) {
		ResponseEntity resEntity;
		try {
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			Event event = new Event();
			OnlineMeetingInfo om = new OnlineMeetingInfo();
//			OnlineMeetingProviderType.
			graphClientWrapper.getGraphServiceClient().users("").events().buildRequest().post(event);
			resEntity = new ResponseEntity(HttpStatus.CREATED);
		}catch (Throwable e) {
			resEntity = new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
}
