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

@RestController("Microsoft_AuthController")
@RequestMapping(Constants.VendorPath.MICROSOFT +"/auth")
public class AuthController {
	
	@Autowired
	private MicrosoftConfiguration msConfig;
	
	@GetMapping("/redirectUri")
	public void sendRedirectResponse(HttpServletRequest request, HttpServletResponse response) {
		
		try {			
			IAuthenticationResult result = (IAuthenticationResult) request.getSession().getAttribute("principal");			
			response.sendRedirect(msConfig.getRedirectResponseUri()+"?emailId="+result.account().username()+"&"+"sessionId="+request.getSession().getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
//		return request.getHeader(HttpHeaders.COOKIE);
		
		
	}
	
	public String getSessionId(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.COOKIE);
	}

}
