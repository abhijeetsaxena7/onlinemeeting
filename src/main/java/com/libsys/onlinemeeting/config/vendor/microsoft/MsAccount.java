package com.libsys.onlinemeeting.config.vendor.microsoft;

import java.io.Serializable;

import com.microsoft.aad.msal4j.IAccount;

/**
 * Custom class that implemnts IAccount interface so that it can be serialized and stored in database
 * @author Abhijeet Saxena
 *
 */
public class MsAccount implements IAccount,Serializable{
	String homeAccountId;
	String environment;
	String username;
	public MsAccount(){
	}
	public MsAccount(IAccount account) {
		this.homeAccountId = account.homeAccountId();
		this.environment = account.environment();
		this.username = account.username();
	}
	@Override
	public String homeAccountId() {
		return homeAccountId;
	}

	@Override
	public String environment() {
		return environment;
	}

	@Override
	public String username() {
		return username;
	}
	public String getHomeAccountId() {
		return homeAccountId;
	}
	public void setHomeAccountId(String homeAccountId) {
		this.homeAccountId = homeAccountId;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
