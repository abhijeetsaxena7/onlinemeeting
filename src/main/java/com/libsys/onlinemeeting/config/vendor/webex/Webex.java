package com.libsys.onlinemeeting.config.vendor.webex;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.StateData;
import com.libsys.onlinemeeting.config.Vendor;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Messages;
import com.microsoft.aad.msal4j.IAuthenticationResult;

@Component
public class Webex implements Vendor{
	private SessionManagementHelper sessionManagementHelper;
	private WebexConfiguration webexConfiguration;
	private HelperMethods helperMethods;
	
	@Autowired
	public Webex(SessionManagementHelper sessionManagementHelper, WebexConfiguration webexConfiguration, HelperMethods helperMethods) {
		this.sessionManagementHelper = sessionManagementHelper;
		this.webexConfiguration = webexConfiguration;
		this.helperMethods = helperMethods;
	}

	@Override
	public void sendAuthRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String state = UUID.randomUUID().toString();
		String nonce = UUID.randomUUID().toString();
		// state parameter to validate response from Authorization server and nonce
		// parameter to validate idToken
		sessionManagementHelper.storeStateAndNonceInSession(request.getSession(), state, nonce);
		response.setStatus(302);
		String authUrl = buildAuthorizationUrl(state);
		response.sendRedirect(authUrl);
	}
	
	private String buildAuthorizationUrl(String state) {
		LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		queryParams.add(Constants.QueryParams.RESPONSE_TYPE, "code");
		queryParams.add(Constants.QueryParams.CLIENT_ID, webexConfiguration.getClientId());
		queryParams.add(Constants.QueryParams.REDIRECT_URI, webexConfiguration.getRedirectUri());
		queryParams.add(Constants.QueryParams.STATE, state);
		queryParams.add(Constants.QueryParams.SCOPE, getAllScopes());
		
		return helperMethods.getUri(webexConfiguration.getAuthUrl(), queryParams);
	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME) != null;
	}
	
	@Override
	public boolean isAccessTokenExpired(HttpServletRequest request) {
		AccessToken accessToken = (AccessToken) sessionManagementHelper.getAuthSessionObject(request);
		Date expireDate = new Date(accessToken.getExpiresIn());
		return expireDate.before(new Date());
	}

	
	private String getAllScopes() {
		StringBuilder scopeBuilder = new StringBuilder();
		//TODO
		return scopeBuilder.toString();
	}

	@Override
	public boolean containsAuthCode(HttpServletRequest httpRequest) {
        Map<String, String[]> httpParameters = httpRequest.getParameterMap();

        boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
        boolean containsErrorData = httpParameters.containsKey("error");
        boolean containsCode = httpParameters.containsKey("code");

        return isPostRequest && containsErrorData || containsCode ;
	} 

	@Override
	public void processAuthCodeRedirect(HttpServletRequest httpRequest) throws Throwable {
		String authCode = httpRequest.getParameter(Constants.QueryParams.AUTH_CODE);
		String state = httpRequest.getParameter(Constants.QueryParams.STATE);
		sessionManagementHelper.validateState(httpRequest.getSession(),state);
		
		AccessToken accessToken = getAccessToken(authCode,false,null);
		sessionManagementHelper.setSessionPrincipal(httpRequest, accessToken);
	}
	
	private AccessToken getAccessToken(String authCode, boolean acquireRefreshToken, String refreshToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, Constants.HeaderValue.APPLICATION_X_WWW_FORM_ENCODED);
		
		LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		if(acquireRefreshToken) {
			queryParams.add(Constants.QueryParams.GRANT_TYPE, "refreshToken");
			queryParams.add(Constants.QueryParams.REFRESH_TOKEN, refreshToken);
		}else {
			queryParams.add(Constants.QueryParams.GRANT_TYPE, "code");	
			queryParams.add(Constants.QueryParams.AUTH_CODE, authCode);
		}
		
		queryParams.add(Constants.QueryParams.CLIENT_ID, webexConfiguration.getClientId());
		queryParams.add(Constants.QueryParams.CLIENT_SECRET, webexConfiguration.getSecret());
		queryParams.add(Constants.QueryParams.REDIRECT_URI, webexConfiguration.getRedirectUri());
		queryParams.add(Constants.QueryParams.SCOPE, getAllScopes());
		
		
		String url = helperMethods.getUri(webexConfiguration.getTokenUrl(), queryParams);
		HttpEntity entity = new HttpEntity(headers);
		try {
			ResponseEntity<AccessToken> response = restTemplate.exchange(url, HttpMethod.POST,entity,AccessToken.class);
			return response.getBody();
		}catch(Throwable e) {
			throw new RuntimeException(Messages.FAILED_TO_ACQUIRE_ACCESS_TOKEN,e);
		}		
	}

	@Override
	public void acquireTokenFromRefreshToken(HttpServletRequest httpRequest) {
		AccessToken accessToken = (AccessToken) sessionManagementHelper.getAuthSessionObject(httpRequest);
		AccessToken newAccessToken = getAccessToken(null, true, accessToken.getRefreshToken());
		sessionManagementHelper.setSessionPrincipal(httpRequest, newAccessToken);
	}
	
	public String getAccessTokenValueFromSession(HttpServletRequest request) {
		return ((AccessToken)sessionManagementHelper.getAuthSessionObject(request)).getAccessToken();
	}
}
