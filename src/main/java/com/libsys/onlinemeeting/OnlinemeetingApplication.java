package com.libsys.onlinemeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
public class OnlinemeetingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlinemeetingApplication.class, args);
		
		MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		Set<String> hashSet = new HashSet<>();
		
		hashSet.add("scope:read scioe:write");
//		hashSet.add("scope:write");
		params.put("scope", new ArrayList<String>(hashSet));
		
//		UriComponentsBuilder.fromHttpUrl("https://webexapi.com/authorize").queryParam("scope",hashSet).build().encode().toString();
//		UriComponents uri = UriComponentsBuilder.fromHttpUrl("https://webexapi.com/authorize").queryParams(params).build();
	}

}
