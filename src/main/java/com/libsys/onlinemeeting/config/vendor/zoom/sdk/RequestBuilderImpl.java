package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import javax.ws.rs.core.UriBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RequestBuilderImpl<T> implements RequestBuilder<T>{
	String endPoint = "https://api.zoom.us/v2";
	HttpHeaders httpHeaders;
	Class<T> responseClass;
	UriBuilder uriBuilder;
	Request request;
	
	public RequestBuilderImpl(String path, String accessToken, Class<T> responseClass) {		
		request = new RequestImpl();
		httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer "+ accessToken);
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		uriBuilder.path(endPoint+path);
	}

	@Override
	public RequestBuilder<T> queryParam(String key, String value) {
		uriBuilder.queryParam(key, value);
		return this;
	}

	@Override
	public RequestBuilder<T> path(String path) {
		uriBuilder.path(path);
		return this;
	}
	
	@Override
	public RequestBuilder<T> addHeader(String name, String value) {
		httpHeaders.add(name, value);
		return this;
	}

	@Override
	public T post(T body) {
		return request.post(httpHeaders, uriBuilder.build(),body, responseClass);
	}

	@Override
	public T put(T body) {
		return request.put(httpHeaders, uriBuilder.build(), body, responseClass);
	}

	@Override
	public T get() {
		return request.get(httpHeaders, uriBuilder.build(), responseClass);
	}

	@Override
	public void delete() {
		request.delete(httpHeaders, uriBuilder.build());
	}
	
	@Override
	public T patch(T body) {
		return request.patch(httpHeaders, uriBuilder.build(), body, responseClass);
	}	

}
