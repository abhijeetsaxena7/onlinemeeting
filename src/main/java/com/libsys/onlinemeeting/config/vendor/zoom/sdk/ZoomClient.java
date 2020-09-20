package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ZoomImpl.Builder;

public interface ZoomClient {
	RequestBuilder<Meeting> meeting();
	RequestBuilder<User> user();
	RequestBuilder<Meeting> meeting(String userId);
	RequestBuilder<User> user(String userId);

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(String accessToken) {
		return new Builder(accessToken);
	}
}
