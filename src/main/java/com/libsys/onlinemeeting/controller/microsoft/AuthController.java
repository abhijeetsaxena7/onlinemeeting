package com.libsys.onlinemeeting.controller.microsoft;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.auth.AuthObjectHandler;
import com.libsys.onlinemeeting.config.auth.AuthTbl;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.AuthModel;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftConfiguration;
import com.libsys.onlinemeeting.config.vendor.microsoft.MsAccount;
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
	@Autowired
	private AuthObjectHandler authObjHandler;
	@Autowired
	private HelperMethods helper;
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
			IAuthenticationResult result = (IAuthenticationResult) request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME);
			
			Object tokenCache = request.getSession().getAttribute(Constants.Session.TOKEN_CACHE);
			MsAccount account = new MsAccount(result.account());
			AuthModel authModel = new AuthModel(account,tokenCache,result.accessToken(),result.expiresOnDate().getTime());
			authObjHandler.addAuthTbl(request.getSession().getId(), Constants.Vendors.Microsoft.getId(), authModel);

			response.sendRedirect(msConfig.getRedirectResponseUri()+"?emailId="+result.account().username()+"&"+"sessionId="+request.getSession().getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
}
