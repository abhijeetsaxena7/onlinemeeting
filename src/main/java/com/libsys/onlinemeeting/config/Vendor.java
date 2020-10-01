package com.libsys.onlinemeeting.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.libsys.onlinemeeting.config.constant.Constants;

/**
 * 
 * @author Abhijeet Saxena 
 * @apiNote This interface contains basic methods that are
 *         required to implement the OAuth2.0 flow.
 */
public interface Vendor {

	void sendAuthRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException;

	boolean isAuthenticated(HttpServletRequest request);

	boolean isAccessTokenExpired(HttpServletRequest httpRequest);

	boolean containsAuthCode(HttpServletRequest httpRequest);

	void processAuthCodeRedirect(HttpServletRequest httpRequest) throws Throwable;

	void acquireTokenFromRefreshToken(HttpServletRequest httpRequest) throws Throwable;

	void deserializeAndSetInSession(String authObject,HttpServletRequest httpRequest);
	
}
