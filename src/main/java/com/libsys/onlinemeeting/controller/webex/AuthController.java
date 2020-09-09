package com.libsys.onlinemeeting.controller.webex;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;
import com.libsys.onlinemeeting.config.vendor.webex.WebexConfiguration;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Person;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Spark;

@RestController("Webex_AuthController")
@RequestMapping(Constants.VendorPath.WEBEX + "/auth")
public class AuthController {

	private WebexConfiguration webexConfig;
	private Webex webex;
	
	@Autowired
	public AuthController(WebexConfiguration webexConfig, Webex webex) {
		this.webexConfig = webexConfig;
		this.webex = webex;
	}
	/**
	 * @param request Http servlet request 
	 * @param response Http servlet response
	 * 
	 * This method gets the redirect response. 
	 * Response is then redirected to redirectResponseUri with use emailId and sessionId in params 
	 */
	@GetMapping("/redirectUri")
	public void sendRedirectResponse(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = webex.getAccessTokenValueFromSession(request);
		
		Spark spark = Spark.builder().accessToken(accessToken).build();
		Person person = spark.people().path("/me").get();
		
		try {			
			response.sendRedirect(webexConfig.getRedirectResponseUri()+"?email = "+person.getEmails()[0]+"&sessionId="+request.getSession().getId());
			System.out.println(request.getSession().getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

}
