package com.libsys.onlinemeeting.config.vendor.microsoft;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("aad")
public class MicrosoftConfiguration {
	private String clientId;
	private String secret;
	private String redirectUri;
	private String redirectResponseUri;
	private String authority;
	private String graphEndpointHost;	
	private String authEndpoint;
	private String tokenEndpoint;	
	
	
	public String getAuthority() {
		if (!authority.endsWith("/")) {
            authority += "/";
        }
        return authority;
	}
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
	public String getGraphEndpointHost() {
		return graphEndpointHost;
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
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public void setGraphEndpointHost(String graphEndpointHost) {
		this.graphEndpointHost = graphEndpointHost;
	}
	public String getAuthEndpoint() {
		return authEndpoint;
	}
	public void setAuthEndpoint(String authEndpoint) {
		this.authEndpoint = authEndpoint;
	}
	public String getTokenEndpoint() {
		return tokenEndpoint;
	}
	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}
	
	
}
