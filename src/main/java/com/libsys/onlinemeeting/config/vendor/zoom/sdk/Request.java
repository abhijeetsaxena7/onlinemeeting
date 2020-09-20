package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import java.net.URI;

import org.springframework.http.HttpHeaders;

public interface Request {
	public <T> T get(HttpHeaders headers, URI uri, Class<T> responseClass);
	public <T> T post(HttpHeaders headers, URI uri, T body, Class<T> responseClass);
	public <T> T put(HttpHeaders headers, URI uri, T body, Class<T> responseClass);
	public <T> T patch(HttpHeaders headers, URI uri, T body, Class<T> responseClass);
	public void delete(HttpHeaders headers, URI uri);
}
