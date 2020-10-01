package com.libsys.onlinemeeting.config.vendor.microsoft;

import java.io.Serializable;

import com.microsoft.aad.msal4j.IAuthenticationResult;

public class AuthModel implements Serializable {
	private MsAccount account;
	private Object tokenCache;
	private String accessToken;
	private long expireTime;
	
	public AuthModel() {
	}
	
	public AuthModel(MsAccount account, Object tokenCache, String accessToken,long expireTime) {
		this.account = account;
		this.tokenCache = tokenCache;
		this.accessToken = accessToken;
		this.expireTime = expireTime;
	}
	
	public MsAccount getAccount() {
		return account;
	}

	public void setAccount(MsAccount account) {
		this.account = account;
	}

	public Object getTokenCache() {
		return tokenCache;
	}
	public void setTokenCache(Object tokenCache) {
		this.tokenCache = tokenCache;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
}
