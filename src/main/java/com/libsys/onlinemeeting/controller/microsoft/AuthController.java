package com.libsys.onlinemeeting.controller.microsoft;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftConfiguration;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * 
 * @author Abhijeet Saxena
 *	Contains authorization methods related to redirect in OAuth2.0 process.
 */
@RestController("Microsoft_AuthController")
@RequestMapping(Constants.VendorPath.MICROSOFT +"/auth")
public class AuthController {
	
	@Autowired
	private MicrosoftConfiguration msConfig;
	
	/**
	 * @param request Http servlet request 
	 * @param response Http servlet response
	 * 
	 * This method gets the redirect response. 
	 * Response is then redirected to redirectResponseUri with use emailId and sessionId in params 
	 */
	@GetMapping("/redirectUri")
	public void sendRedirectResponse(HttpServletRequest request, HttpServletResponse response) {
		
		try {			
			IAuthenticationResult result = (IAuthenticationResult) request.getSession().getAttribute("principal");			
			response.sendRedirect(msConfig.getRedirectResponseUri()+"?emailId="+result.account().username()+"&"+"sessionId="+request.getSession().getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
}
