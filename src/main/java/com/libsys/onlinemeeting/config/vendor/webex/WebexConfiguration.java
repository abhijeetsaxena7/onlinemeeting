package com.libsys.onlinemeeting.config.vendor.webex;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("cisco")
public class WebexConfiguration {
	private String clientId;
	private String secret;
	private String redirectUri;
	private String redirectResponseUri;
	private String authUrl; //https://webexapis.com/v1/authorize
	private String tokenUrl;	//https://webexapis.com/v1/access_token
	
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
	
	

	
}
