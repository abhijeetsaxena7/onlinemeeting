package com.libsys.onlinemeeting.config.vendor.microsoft;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.net.HttpHeaders;
import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.StateData;
import com.libsys.onlinemeeting.config.Vendor;
import com.libsys.onlinemeeting.config.auth.AuthObjectHandler;
import com.libsys.onlinemeeting.config.auth.AuthTbl;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Messages;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftScopes.BaseScope;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IConfidentialClientApplication;
import com.microsoft.aad.msal4j.Prompt;
import com.microsoft.aad.msal4j.ResponseMode;
import com.microsoft.aad.msal4j.SilentParameters;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

@Component
public class Microsoft implements Vendor {
	private SessionManagementHelper sessionManagementHelper;
	private MicrosoftConfiguration msConfig;
	private AuthObjectHandler authObjectHandler;
	private HelperMethods helper;
	@Autowired
	public Microsoft(SessionManagementHelper sessionManagementHelper, MicrosoftConfiguration msConfig,AuthObjectHandler authObjectHandler,HelperMethods helper) {
		this.sessionManagementHelper = sessionManagementHelper;
		this.msConfig = msConfig;
		this.authObjectHandler = authObjectHandler;
		this.helper = helper;
	}

	/**
	 * Redirect to login page to fetch authorization code
	 */
	@Override
	public void sendAuthRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		String state = UUID.randomUUID().toString();
//		String nonce = UUID.randomUUID().toString();
		// state parameter to validate response from Authorization server and nonce
		// parameter to validate idToken
//		sessionManagementHelper.storeStateAndNonceInSession(request.getSession(), state, nonce);

		response.setStatus(302);
		String authorizationCodeUrl = getAuthorizationCodeUrl(request.getParameter("claims"), getAllScopes(),
				msConfig.getRedirectUri(), null, null);
		response.sendRedirect(authorizationCodeUrl);

	}

	/**
	 * Build the authorization Url based on the given parameters
	 * @param claims
	 * @param scopes
	 * @param redirectUri
	 * @param state
	 * @param nonce
	 * @return
	 */
	private String getAuthorizationCodeUrl(String claims, Set<String> scopes, String redirectUri, String state,
			String nonce) {
		AuthorizationRequestUrlParameters parameters = AuthorizationRequestUrlParameters
				.builder(redirectUri, getAllScopes()).responseMode(ResponseMode.QUERY).prompt(Prompt.SELECT_ACCOUNT)
				./* state(state).nonce(nonce). */claimsChallenge(claims).build();

		ConfidentialClientApplication cca = null;
		try {
			cca = createClientApplication();
			return cca.getAuthorizationRequestUrl(parameters).toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return instance of ConfidentialClientApplication
	 * @throws MalformedURLException
	 */
	private ConfidentialClientApplication createClientApplication() throws MalformedURLException {
		return ConfidentialClientApplication
				.builder(msConfig.getClientId(), ClientCredentialFactory.createFromSecret(msConfig.getSecret()))
				.authority(msConfig.getAuthority()).build();

	}

	/**
	 * Check if user has already logged in or not
	 */
	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME) != null || request.getHeader(HttpHeaders.AUTHORIZATION)!=null;
	}

	/**
	 * validates whether access token has expired or not
	 */
	@Override
	public boolean isAccessTokenExpired(HttpServletRequest request) {
		AuthModel authModel = (AuthModel) helper.getAuthObjectFromSession(request);
		return new Date(authModel.getExpireTime()).before(new Date());
	}

	/**
	 * Return auth scopes based on the format
	 * @param scopes
	 * @return
	 */
	private Set<String> getAuthScopes(BaseScope[] scopes) {
		Set<String> scopeSet = new HashSet<>();
		for (BaseScope scope : scopes) {
			scopeSet.add(scope.getAuthValue());
		}
		return scopeSet;
	}

	/**
	 * Returns all scopes in a set to be granted permission from user
	 * @return
	 */
	public Set<String> getAllScopes() {
		Set<String> scopes = new HashSet<String>();
		scopes.addAll(getAuthScopes(MicrosoftScopes.User.Create.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.User.Update.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.User.Delete.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.User.AddRole.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.Create.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.Delete.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.AddMember.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.AddOwner.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.DeleteMember.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Group.DeleteOwner.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Meeting.Create.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Meeting.Delete.values()));
		scopes.addAll(getAuthScopes(MicrosoftScopes.Event.Create.values()));
		return scopes;
	}

	public Set<String> getReqScopes(BaseScope[] scopes) {
		Set<String> scopeSet = new HashSet<>();
		for (BaseScope scope : scopes) {
			scopeSet.add(scope.getReqValue());
		}
		return scopeSet;
	}

	/**
	 * Validates if the request contains the authorization code
	 */
	@Override
	public boolean containsAuthCode(HttpServletRequest httpRequest) {
		Map<String, String[]> httpParameters = httpRequest.getParameterMap();

		boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
		boolean containsErrorData = httpParameters.containsKey("error");
		boolean containIdToken = httpParameters.containsKey("id_token");
		boolean containsCode = httpParameters.containsKey("code");

		return isPostRequest && containsErrorData || containsCode || containIdToken;

	}
	
	/**
	 * Acquire the access token from using the authorization code
	 */
	@Override
	public void processAuthCodeRedirect(HttpServletRequest httpRequest) throws Throwable {
		Map<String, List<String>> params = new HashMap<>();
		for (String key : httpRequest.getParameterMap().keySet()) {
			params.put(key, Collections.singletonList(httpRequest.getParameterMap().get(key)[0]));
		}
		// validate that state in response equals to state in request
//		StateData stateData = sessionManagementHelper.validateState(httpRequest.getSession(),
//				params.get(Constants.QueryParams.STATE).get(0));

		String currentUri = httpRequest.getRequestURL().toString();
		String queryStr = httpRequest.getQueryString();
		String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");
		String authCode = httpRequest.getParameter(Constants.QueryParams.AUTH_CODE);
//		AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
//		if (isAuthenticationSuccessful(authResponse)) {
//			AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
			// validate that OIDC Auth Response matches Code Flow (contains only requested
			// artifacts)
//			validateAuthRespMatchesAuthCodeFlow(oidcResponse);

			IAuthenticationResult result = getAuthResultByAuthCode(httpRequest, authCode,
					currentUri);

			// validate nonce to prevent reply attacks (code maybe substituted to one with
			// broader access)
//			validateNonce(stateData, getNonceClaimValueFromIdToken(result.idToken()));

			sessionManagementHelper.setSessionPrincipal(httpRequest, result);
//		} else {
//			AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
//			throw new Exception(String.format("Request for auth code failed: %s - %s",
//					oidcResponse.getErrorObject().getCode(), oidcResponse.getErrorObject().getDescription()));
//		}
	}

	private String getNonceClaimValueFromIdToken(String idToken) throws ParseException {
		return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim("nonce");
	}

	/**
	 * Get Access Token using authorization code and store it in session
	 * @param httpRequest
	 * @param authorizationCode
	 * @param currentUri
	 * @return
	 * @throws Throwable
	 */
	private IAuthenticationResult getAuthResultByAuthCode(HttpServletRequest httpRequest,
			String authorizationCode, String currentUri) throws Throwable {
		IAuthenticationResult result;
		ConfidentialClientApplication app;
		try {
			app = createClientApplication();

//			String authCode = authorizationCode.getValue();
			AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(authorizationCode, new URI(currentUri))
					.build();

			Future<IAuthenticationResult> future = app.acquireToken(parameters);

			result = future.get();
		} catch (ExecutionException e) {
			throw e.getCause();
		}

		if (result == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}

		sessionManagementHelper.storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());

		return result;
	}

	private void validateAuthRespMatchesAuthCodeFlow(AuthenticationSuccessResponse oidcResponse) throws Exception {
		if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null
				|| oidcResponse.getAuthorizationCode() == null) {
			throw new Exception(Messages.FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
		}
	}

	private boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
		return authResponse instanceof AuthenticationSuccessResponse;
	}

	private void validateNonce(StateData stateData, String nonce) throws Exception {
		if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
			throw new Exception(Messages.FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
		}
	}

	/**
	 * get access token using refresh token if access token has expired
	 */
	@Override
	public void acquireTokenFromRefreshToken(HttpServletRequest httpRequest) throws Throwable {
		IAuthenticationResult authResult = getAuthResultBySilentFlow(httpRequest);
	}

	/**
	 * Get IAuthneticationResult object using silent flow
	 * @param httpRequest
	 * @return
	 * @throws Throwable
	 */
	public IAuthenticationResult getAuthResultBySilentFlow(HttpServletRequest httpRequest) throws Throwable {

//		IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(httpRequest);

		AuthModel authModel = (AuthModel) httpRequest.getSession().getAttribute(Constants.Session.AUTH_OBJECT);
		IConfidentialClientApplication app = createClientApplication();

//		Object tokenCache = httpRequest.getSession().getAttribute(Constants.Session.TOKEN_CACHE);
		Object tokenCache = authModel.getTokenCache();
		if (tokenCache != null) {
			app.tokenCache().deserialize(tokenCache.toString());
		}

		SilentParameters parameters = SilentParameters.builder(getAllScopes(), authModel.getAccount()).build();
		CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);
		IAuthenticationResult updatedResult = future.get();

		// update session with latest token cache
//		sessionManagementHelper.storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());

		authModel.setAccessToken(updatedResult.accessToken());
		authModel.setExpireTime(updatedResult.expiresOnDate().getTime());
		authModel.setTokenCache(app.tokenCache().serialize());
		authModel.setAccount(new MsAccount(updatedResult.account()));
		
		//update in sesssion
		httpRequest.getSession().setAttribute(Constants.Session.AUTH_OBJECT,authModel);
		authObjectHandler.updateAuthTbl(httpRequest.getHeader(HttpHeaders.AUTHORIZATION), authModel);
		return updatedResult;
	}

	/**
	 * Deserialize the auth object and store it in session
	 */
	@Override
	public void deserializeAndSetInSession(String authObject,HttpServletRequest httpRequest) {
		AuthModel authModel = (AuthModel) helper.getObjectFromString(authObject, AuthModel.class);
		httpRequest.getSession().setAttribute(Constants.Session.AUTH_OBJECT, authModel);
	}

	/**
	 * returns access token from auth object stored in session
	 * @param request
	 * @return
	 */
	public String getAccessTokenFromSession(HttpServletRequest request) {
		return ((AuthModel)sessionManagementHelper.getAuthObjectFromSession(request)).getAccessToken();
	}

}
