package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

public class ZoomImpl implements ZoomClient{

	private String accessToken;
	
	private ZoomImpl(String accessToken) {
		this.accessToken = accessToken;		
	}

	public static class Builder{
		private String accessToken;
		
		public Builder() {
		}		
		
		public Builder(String accessToken) {
			this.accessToken = accessToken;
		}
		
		public Builder setAccessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		
		public ZoomImpl build() {
			return new ZoomImpl(accessToken);
		}
	}
	
	@Override
	public RequestBuilder<Meeting> meeting(String userId) {
		return new RequestBuilderImpl("/users/"+userId+"/meetings",accessToken,Meeting.class);
	}
	
	@Override
	public RequestBuilder<Meeting> meeting() {
		return new RequestBuilderImpl("/meetings",accessToken,Meeting.class);
	}

	@Override
	public RequestBuilder<User> user() {
		return new RequestBuilderImpl("/users/"+"me",accessToken,User.class);
	}

	@Override
	public RequestBuilder<User> user(String userId) {
		return new RequestBuilderImpl("/users/"+userId,accessToken,User.class);
	}
}
