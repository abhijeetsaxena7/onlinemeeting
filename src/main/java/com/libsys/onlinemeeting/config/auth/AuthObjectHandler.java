package com.libsys.onlinemeeting.config.auth;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Messages;
import com.libsys.onlinemeeting.config.vendor.microsoft.AuthModel;

@Service
public class AuthObjectHandler {
	@Autowired
	AuthRepo authRepo;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	HelperMethods helper;
	
	public AuthTbl addAuthTbl(int vendorId, Object authObject) {
		return addAuthTbl(UUID.randomUUID().toString(),vendorId, authObject);
	}
	
	public AuthTbl addAuthTbl(String sessionId,int vendorId, Object authObject){
		AuthTbl authTbl = new AuthTbl();
		authTbl.setVendorId(vendorId);		
		authTbl.setAuthObject(helper.getStringForObject(authObject));
		authTbl.setSessionId(sessionId);
		authRepo.save(authTbl);
		return authTbl;
	}
	
	public AuthTbl updateAuthTbl(String sessionId, Object authObject) {
		Optional<AuthTbl> optional = authRepo.findById(sessionId);
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("No Auth Object found for given session");
		}
		
		AuthTbl authTbl = optional.get();
		authTbl.setAuthObject(helper.getStringForObject(authObject));
		authRepo.save(authTbl);
		return authTbl;
	} 
	
	public AuthTbl getAuthTbl(String sessionId) {
		Optional<AuthTbl> optional = authRepo.findById(sessionId);
		return optional.orElse(null);
	}

	@Transactional
	public void deleteAuthTbl(String sessionId) {
		authRepo.deleteById(sessionId);
	}

	public String getAuthObject(HttpServletRequest httpRequest) {
		int vendorId = helper.getVendor(httpRequest);
		AuthTbl authTbl = getAuthTbl(httpRequest.getHeader(HttpHeaders.AUTHORIZATION));
		if(authTbl==null) {
			throw new IllegalArgumentException(Messages.INVALID_AUTHORIZATION_VALUE);
		}
		
		return authTbl.getAuthObject();
	}
}
