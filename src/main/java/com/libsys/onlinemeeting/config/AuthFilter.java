package com.libsys.onlinemeeting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.libsys.onlinemeeting.config.constant.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author abhij
 * Filter is used to manage redirection of requests based on different stages of OAuth2.0
 */
@Component
public class AuthFilter implements Filter {

	@Autowired
	private HelperMethods helper;
	@Autowired
	private VendorFactory vendorFactory;

	List<String> excludedUrls = new ArrayList<>();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			String path = httpRequest.getServletPath();
			
			//exclude paths from filter 
			if (excludedUrls.contains(path)) {
				chain.doFilter(request, response);
				return;
			}

			try {
				//get vendor id to obtain instance of vendor
				if (!helper.hasVendor(httpRequest)) {
					helper.setVendor(httpRequest, path);
				}
				Vendor vendor = vendorFactory.getInstance(helper.getVendor(httpRequest));

				//do not change the order of checks
				
				//check if auth code exists in param, use it to obtain access token
				if (vendor.containsAuthCode(httpRequest)) {
					vendor.processAuthCodeRedirect(httpRequest);
					chain.doFilter(request, response);
					return;
				}
				//if request is not authenticated, then send to login page
				if (!vendor.isAuthenticated(httpRequest)) {
					vendor.sendAuthRedirect(httpRequest, httpResponse);
					return;
				}
				//if access token expired, then obtain new token using refresh token.
				if(vendor.isAccessTokenExpired(httpRequest)) {
					vendor.acquireTokenFromRefreshToken(httpRequest);
					chain.doFilter(request, response);
					return;
				}

			} catch (Throwable e) {
				httpResponse.setStatus(500);
				System.out.println(e.getMessage());
				request.setAttribute("error", e.getMessage());
				request.getRequestDispatcher("/error").forward(request, response);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

}
