package com.libsys.onlinemeeting.config.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AuthTbl {
	@Id
	private String sessionId;
	private int vendorId;
	@Column(columnDefinition = "longblob")
	private String authObject;
	
	public AuthTbl() {
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public String getAuthObject() {
		return authObject;
	}

	public void setAuthObject(String authObject) {
		this.authObject = authObject;
	}
}
