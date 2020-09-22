package com.libsys.onlinemeeting.config.vendor.zoom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("zoom")
public class ZoomConfiguration {
	private String clientId;
	private String secret;
	private String redirectUri;
	private String redirectResponseUri;
	private String authUrl; //https://zoom.us/oauth/authorize
	private String tokenUrl;	//https://zoom.us/oauth/access_token
	private String attendanceResponseUrl;
	
	public String getClientId() {
		return clientId;
	}
	public String getSecret() {
		return secret;
	}
	public String getRedirectUri() {
		return redirectUri;
	}
	public String getRedirectResponseUri() {
		return redirectResponseUri;
	}
	public String getAuthUrl() {
		return authUrl;
	}
	public String getTokenUrl() {
		return tokenUrl;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	public void setRedirectResponseUri(String redirectResponseUri) {
		this.redirectResponseUri = redirectResponseUri;
	}
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
	public String getAttendanceResponseUrl() {
		return attendanceResponseUrl;
	}
	public void setAttendanceResponseUrl(String attendanceResponseUrl) {
		this.attendanceResponseUrl = attendanceResponseUrl;
	}
}
