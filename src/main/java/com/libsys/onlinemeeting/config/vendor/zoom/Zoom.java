package com.libsys.onlinemeeting.config.vendor.zoom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
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
import com.libsys.onlinemeeting.config.auth.AuthObjectHandler;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Messages;
import com.microsoft.aad.msal4j.IAuthenticationResult;

@Component
public class Zoom implements Vendor {
	private SessionManagementHelper sessionManagementHelper;
	private ZoomConfiguration zoomConfiguration;
	private HelperMethods helperMethods;
	private AuthObjectHandler authObjHandler;

	@Autowired
	public Zoom(SessionManagementHelper sessionManagementHelper, ZoomConfiguration zoomConfiguration,
			HelperMethods helperMethods, AuthObjectHandler authObjHandler) {
		this.sessionManagementHelper = sessionManagementHelper;
		this.zoomConfiguration = zoomConfiguration;
		this.helperMethods = helperMethods;
		this.authObjHandler = authObjHandler;
	}

	/**
	 * redirect request to login page to fetch authorization codes
	 */
	@Override
	public void sendAuthRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//		String state = UUID.randomUUID().toString();
		//		String nonce = UUID.randomUUID().toString();
		// state parameter to validate response from Authorization server and nonce
		// parameter to validate idToken
		//		sessionManagementHelper.storeStateAndNonceInSession(request.getSession(), state, nonce);
		response.setStatus(302);
		String authUrl = buildAuthorizationUrl(null);
		response.sendRedirect(authUrl);
	}

	/**
	 * Build the authorization url based on stored configuration
	 * 
	 * @param state
	 * @return
	 */
	private String buildAuthorizationUrl(String state) throws UnsupportedEncodingException {
		LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		queryParams.add(Constants.QueryParams.RESPONSE_TYPE, "code");
		queryParams.add(Constants.QueryParams.CLIENT_ID, zoomConfiguration.getClientId());
		queryParams.add(Constants.QueryParams.REDIRECT_URI,
				helperMethods.encodeUrl(zoomConfiguration.getRedirectUri()));
		if (state != null) {
			queryParams.add(Constants.QueryParams.STATE, state);
		}
		//		queryParams.add(Constants.QueryParams.SCOPE, getAllScopes());

		return helperMethods.getUri(zoomConfiguration.getAuthUrl(), queryParams, false);
	}

	/**
	 * Validate whether user is already logged in or not
	 */
	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME) != null
				|| request.getHeader(HttpHeaders.AUTHORIZATION) != null;
	}

	/**
	 * Validate if access token has expired or not
	 */
	@Override
	public boolean isAccessTokenExpired(HttpServletRequest request) {
		AccessToken accessToken = (AccessToken) helperMethods.getAuthObjectFromSession(request);
		Date expireDate = new Date(accessToken.getExpiresIn());
		return expireDate.before(new Date());
	}

	/**
	 * get all scopes in a string
	 * 
	 * @return
	 */
	private String getAllScopes() {
		StringBuilder scopeBuilder = new StringBuilder();
		scopeBuilder.append(ZoomScopes.Meeting.MEETING_WRITE.value);
		return scopeBuilder.toString();
	}

	/**
	 * validate if the request contains auth code
	 */
	@Override
	public boolean containsAuthCode(HttpServletRequest httpRequest) {
		Map<String, String[]> httpParameters = httpRequest.getParameterMap();

		boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
		boolean containsErrorData = httpParameters.containsKey("error");
		boolean containsCode = httpParameters.containsKey("code");

		return isPostRequest && containsErrorData || containsCode;
	}

	/**
	 * get access token using authorization code amd store it in session
	 */
	@Override
	public void processAuthCodeRedirect(HttpServletRequest httpRequest) throws Throwable {
		String authCode = httpRequest.getParameter(Constants.QueryParams.AUTH_CODE);
		//		String state = httpRequest.getParameter(Constants.QueryParams.STATE);
		//		sessionManagementHelper.validateState(httpRequest.getSession(),state);

		AccessToken accessToken = getAccessToken(authCode, false, null);
		sessionManagementHelper.setSessionPrincipal(httpRequest, accessToken);
	}

	/**
	 * create access token request using auth code or refresh token and fetch it
	 * 
	 * @param authCode
	 * @param acquireRefreshToken
	 * @param refreshToken
	 * @return
	 */
	private AccessToken getAccessToken(String authCode, boolean acquireRefreshToken, String refreshToken)
			throws UnsupportedEncodingException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		//		headers.add(HttpHeaders.CONTENT_TYPE, Constants.HeaderValue.APPLICATION_X_WWW_FORM_ENCODED);
		String authorization = zoomConfiguration.getClientId() + ":" + zoomConfiguration.getSecret();
		headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(authorization.getBytes()));

		LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		if (acquireRefreshToken) {
			queryParams.add(Constants.QueryParams.GRANT_TYPE, "refreshToken");
			queryParams.add(Constants.QueryParams.REFRESH_TOKEN, refreshToken);
		} else {
			queryParams.add(Constants.QueryParams.GRANT_TYPE, "authorization_code");
			queryParams.add(Constants.QueryParams.AUTH_CODE, authCode);
		}

		queryParams.add(Constants.QueryParams.REDIRECT_URI,
				helperMethods.encodeUrl(zoomConfiguration.getRedirectUri()));
		//		queryParams.add(Constants.QueryParams.SCOPE, getAllScopes());

		String url = helperMethods.getUri(zoomConfiguration.getTokenUrl(), queryParams, false);
		HttpEntity entity = new HttpEntity(queryParams, headers);
		try {
			ResponseEntity<AccessToken> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					AccessToken.class);
			return response.getBody();
		} catch (Throwable e) {
			throw new RuntimeException(Messages.FAILED_TO_ACQUIRE_ACCESS_TOKEN, e);
		}
	}

	/**
	 * get access token using refresh token and update auth object in db
	 */
	@Override
	public void acquireTokenFromRefreshToken(HttpServletRequest httpRequest) throws UnsupportedEncodingException {
		AccessToken accessToken = (AccessToken) sessionManagementHelper.getSessionPrincipal(httpRequest);
		AccessToken newAccessToken = getAccessToken(null, true, accessToken.getRefreshToken());

		authObjHandler.updateAuthTbl(httpRequest.getHeader(HttpHeaders.AUTHORIZATION), newAccessToken);
		httpRequest.getSession().setAttribute(Constants.Session.AUTH_OBJECT, accessToken);
	}

	/**
	 * returns access token stored in auth object in session
	 * 
	 * @param request
	 * @return
	 */
	public String getAccessTokenValueFromSession(HttpServletRequest request) {
		return ((AccessToken) sessionManagementHelper.getAuthObjectFromSession(request)).getAccessToken();
	}

	/**
	 * Store authentication details for user in database
	 * 
	 * @param request
	 */
	public void storeAccessTokenInDb(HttpServletRequest request) {
		AccessToken accessToken = (AccessToken) sessionManagementHelper.getSessionPrincipal(request);
		authObjHandler.addAuthTbl(request.getSession().getId(), Constants.Vendors.Webex.getId(), accessToken);
	}

	/**
	 * deserialize auth object and set the object in session
	 */
	@Override
	public void deserializeAndSetInSession(String authObject, HttpServletRequest httpRequest) {
		AccessToken accessToken = (AccessToken) helperMethods.getObjectFromString(authObject, AccessToken.class);
		httpRequest.getSession().setAttribute(Constants.Session.AUTH_OBJECT, accessToken);
	}
}
