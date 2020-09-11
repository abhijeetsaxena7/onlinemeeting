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

import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.StateData;
import com.libsys.onlinemeeting.config.Vendor;
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

	@Autowired
	public Microsoft(SessionManagementHelper sessionManagementHelper, MicrosoftConfiguration msConfig) {
		this.sessionManagementHelper = sessionManagementHelper;
		this.msConfig = msConfig;
	}

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

	private ConfidentialClientApplication createClientApplication() throws MalformedURLException {
		return ConfidentialClientApplication
				.builder(msConfig.getClientId(), ClientCredentialFactory.createFromSecret(msConfig.getSecret()))
				.authority(msConfig.getAuthority()).build();

	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME) != null;
	}

	@Override
	public boolean isAccessTokenExpired(HttpServletRequest request) {
		IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
		return result.expiresOnDate().before(new Date());
	}

	private Set<String> getAuthScopes(BaseScope[] scopes) {
		Set<String> scopeSet = new HashSet<>();
		for (BaseScope scope : scopes) {
			scopeSet.add(scope.getAuthValue());
		}
		return scopeSet;
	}

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
		return scopes;
	}

	public Set<String> getReqScopes(BaseScope[] scopes) {
		Set<String> scopeSet = new HashSet<>();
		for (BaseScope scope : scopes) {
			scopeSet.add(scope.getReqValue());
		}
		return scopeSet;
	}

	@Override
	public boolean containsAuthCode(HttpServletRequest httpRequest) {
		Map<String, String[]> httpParameters = httpRequest.getParameterMap();

		boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
		boolean containsErrorData = httpParameters.containsKey("error");
		boolean containIdToken = httpParameters.containsKey("id_token");
		boolean containsCode = httpParameters.containsKey("code");

		return isPostRequest && containsErrorData || containsCode || containIdToken;

	}

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
		AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
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

	@Override
	public void acquireTokenFromRefreshToken(HttpServletRequest httpRequest) throws Throwable {
		IAuthenticationResult authResult = getAuthResultBySilentFlow(httpRequest, getAllScopes());
		sessionManagementHelper.setSessionPrincipal(httpRequest, authResult);
	}

	public IAuthenticationResult getAuthResultBySilentFlow(HttpServletRequest httpRequest, Set<String> scopes) throws Throwable {

		IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(httpRequest);

		IConfidentialClientApplication app = createClientApplication();

		Object tokenCache = httpRequest.getSession().getAttribute(Constants.Session.TOKEN_CACHE);
		if (tokenCache != null) {
			app.tokenCache().deserialize(tokenCache.toString());
		}

		SilentParameters parameters = SilentParameters.builder(scopes, result.account()).build();
		CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);
		IAuthenticationResult updatedResult = future.get();

		// update session with latest token cache
		sessionManagementHelper.storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());

		return updatedResult;
	}

}
