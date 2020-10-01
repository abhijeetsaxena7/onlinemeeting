package com.libsys.onlinemeeting.controller.zoom;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.zoom.Zoom;
import com.libsys.onlinemeeting.config.vendor.zoom.ZoomConfiguration;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.Meeting;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.MeetingSetting;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.User;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ZoomClient;

@RestController("Zoom_AuthController")
@RequestMapping(Constants.VendorPath.ZOOM + "/auth")
public class AuthController {
	@Autowired
	ZoomConfiguration zoomConfig;
	@Autowired
	Zoom zoom;
	
	/**
	 * Find user details and redirect the response to reponseUrl.
	 * @param request
	 * @param response
	 */
	@GetMapping("/redirectUri")
	public void sendRedirectResponse(HttpServletRequest request, HttpServletResponse response) {

		String accessToken = zoom.getAccessTokenValueFromSession(request);
		ZoomClient zoomClient = ZoomClient.builder(accessToken).build();
		User user = zoomClient.user().get();
		zoom.storeAccessTokenInDb(request);
		try {
			response.sendRedirect(zoomConfig.getRedirectResponseUri() + "?emailId=" + user.getEmailId() + "&"
					+ "sessionId=" + request.getSession().getId() + "&" + "userId=" + user.getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
