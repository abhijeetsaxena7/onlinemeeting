package com.libsys.onlinemeeting.config;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Constants.Vendors;

/**
 * 
 * @author Abhijeet Saxena
 *	Contains utility methods to be used in project.
 */
@Component
public class HelperMethods {
	
	/**
	 * Check whether vendor attribute is present in session.
	 * @param request
	 * @return
	 */
	public boolean hasVendor(HttpServletRequest request) {
		return request.getSession()!=null && request.getSession().getAttribute(Constants.Attributes.VENDOR)!=null;
	}
	
	/**
	 * Get vendor Id from session attribute
	 * @param request
	 * @return
	 */
	public int getVendor(HttpServletRequest request) {
		return (int)request.getSession().getAttribute(Constants.Attributes.VENDOR);
	}
	
	/**
	 * Set vendor Id in request session attribute based on path.
	 * @param request
	 * @param path
	 */
	public void setVendor(HttpServletRequest request, String path) {
		Vendors vendor=null;
		if(path.contains("/"+Vendors.Microsoft.getPath()+"/")|| path.contains("/login.microsoftonline.com/")){
			vendor = Vendors.Microsoft;
		}else if(path.contains("/"+Vendors.Webex.getPath()+"/") || path.contains("/webexapis.com/")) {
			vendor = Vendors.Webex;
		}
		
		if(vendor==null) {
			throw new RuntimeException("Invalid url path accessed");
		}
		request.getSession(true).setAttribute(Constants.Attributes.VENDOR, vendor.getId());
	}
	
	/**
	 * Build a url based on url and query params. 
	 * @param url
	 * @param queryParams
	 * @return encoded url
	 */
	public String getUri(String url, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams).build().encode().toString();
	}

}
