package com.libsys.onlinemeeting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;
import com.libsys.onlinemeeting.config.vendor.zoom.Zoom;

/**
 * 
 * @author Abhijeet Saxena
 * This class is used to obtain vendor instance based on vendor id.
 */
@Component
public class VendorFactory {
	
	private Microsoft microsoft;
	private Webex webex;
	private Zoom zoom;
	
	@Autowired
	public VendorFactory(Microsoft microsoft, Webex webex, Zoom zoom) {
		this.microsoft = microsoft;
		this.webex = webex;
		this.zoom = zoom;
	}
	
	public Vendor getInstance(int id) {
		if(id == Constants.Vendors.Microsoft.getId()) {
			return microsoft;
		}
		
		if(id == Constants.Vendors.Webex.getId()) {
			return webex;
		}
		
		if(id == Constants.Vendors.Zoom.getId()) {
			return zoom;
		}
		
		throw new RuntimeException("Invalid Vendor value");
	}

}
