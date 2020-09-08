package com.libsys.onlinemeeting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;

@Component
public class VendorFactory {
	
	private Microsoft microsoft;
	private Webex webex;
	
	@Autowired
	public VendorFactory(Microsoft microsoft, Webex webex) {
		this.microsoft = microsoft;
		this.webex = webex;
	}
	
	public Vendor getInstance(int id) {
		if(id == Constants.Vendors.Microsoft.getId()) {
			return microsoft;
		}
		
		if(id == Constants.Vendors.Webex.getId()) {
			return microsoft;
		}
		
		throw new RuntimeException("Invalid Vendor value");
	}

}
