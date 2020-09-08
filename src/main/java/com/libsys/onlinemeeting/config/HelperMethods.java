package com.libsys.onlinemeeting.config;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Constants.Vendors;


@Component
public class HelperMethods {
	
	
	public boolean hasVendor(HttpServletRequest request) {
		return request.getSession()!=null && request.getSession().getAttribute(Constants.Attributes.VENDOR)!=null;
	}
	
	public int getVendor(HttpServletRequest request) {
		return (int)request.getSession().getAttribute(Constants.Attributes.VENDOR);
	}
	
	public void setVendor(HttpServletRequest request, String path) {
		Vendors vendor=null;
		if(path.contains("/"+Vendors.Microsoft.getPath()+"/")){
			vendor = Vendors.Microsoft;
		}else if(path.contains("/"+Vendors.Webex.getPath()+"/")) {
			vendor = Vendors.Webex;
		}
		
		if(vendor==null) {
			throw new RuntimeException("Invalid url path accessed");
		}
		request.getSession(true).setAttribute(Constants.Attributes.VENDOR, vendor.getId());
	}
	
	public String getUri(String url, MultiValueMap<String, String> queryParams) {
		return UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams).build().encode().toString();
	}

}
