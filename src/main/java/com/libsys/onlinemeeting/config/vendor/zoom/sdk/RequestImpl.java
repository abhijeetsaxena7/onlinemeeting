 package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RequestImpl implements Request{
	RestTemplate restTemplate;
	RequestEntity requestEntity;

	public RequestImpl() {
		restTemplate = new RestTemplate();
	}
	
	@Override
	public <T> T get(HttpHeaders headers, URI uri, Class<T> responseClass) {
		requestEntity = new RequestEntity(headers, HttpMethod.GET,uri);
		ResponseEntity<T> response = restTemplate.exchange(requestEntity, responseClass);
		return response.getBody();
	}

	@Override
	public <T> T post(HttpHeaders headers, URI uri, T body, Class<T> responseClass) {
		requestEntity = new RequestEntity(body,headers, HttpMethod.POST,uri);
		ResponseEntity<T> response = restTemplate.exchange(requestEntity, responseClass);
		return response.getBody();
	}

	@Override
	public <T> T put(HttpHeaders headers, URI uri, T body, Class<T> responseClass) {
		requestEntity = new RequestEntity(body,headers, HttpMethod.PUT,uri);
		ResponseEntity<T> response = restTemplate.exchange(requestEntity, responseClass);
		return response.getBody();
	}

	@Override
	public <T> T patch(HttpHeaders headers, URI uri, T body, Class<T> responseClass) {
		requestEntity = new RequestEntity(body,headers, HttpMethod.PATCH,uri);
		ResponseEntity<T> response = restTemplate.exchange(requestEntity, responseClass);
		return response.getBody();
	}

	@Override
	public void delete(HttpHeaders headers, URI uri) {
		requestEntity = new RequestEntity(headers, HttpMethod.PATCH,uri);
		restTemplate.exchange(requestEntity,String.class);
	}

}
