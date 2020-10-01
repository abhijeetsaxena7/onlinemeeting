package com.libsys.onlinemeeting.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Messages;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * 
 * @author Abhijeet Saxena
 * Class containing methods to manage session.
 */
@Component
public class SessionManagementHelper {

	/**
	 * Validate state value obtained in auth request.
	 * @param session
	 * @param state
	 * @return
	 * @throws Exception
	 */
     public StateData validateState(HttpSession session, String state) throws Exception {
        if (StringUtils.isNotEmpty(state)) {
            StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new Exception(Messages.FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

     /**
      * Removes a state from session
      * @param session
      * @param state
      * @return
      */
    private  StateData removeStateFromSession(HttpSession session, String state) {
        Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(Constants.Session.STATES);
        if (states != null) {
            eliminateExpiredStates(states);
            StateData stateData = states.get(state);
            if (stateData != null) {
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    /**
     * remove all expired state objects in the session
     * @param map
     */
    private  void eliminateExpiredStates(Map<String, StateData> map) {
        Iterator<Map.Entry<String, StateData>> it = map.entrySet().iterator();

        Date currTime = new Date();
        while (it.hasNext()) {
            Map.Entry<String, StateData> entry = it.next();
            long diffInSeconds = TimeUnit.MILLISECONDS.
                    toSeconds(currTime.getTime() - entry.getValue().getExpirationDate().getTime());

            if (diffInSeconds > Constants.Session.STATE_TTL) {
                it.remove();
            }
        }
    }

    /**
     * Store state object in the session
     * @param session
     * @param state
     * @param nonce
     */
    public  void storeStateAndNonceInSession(HttpSession session, String state, String nonce) {

        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        if (session.getAttribute(Constants.Session.STATES) == null) {
            session.setAttribute(Constants.Session.STATES, new HashMap<String, StateData>());
        }
        ((Map<String, StateData>) session.getAttribute(Constants.Session.STATES)).put(state, new StateData(nonce, new Date()));
    }

    /**
     * Store access token in token cache
     * @param httpServletRequest
     * @param tokenCache
     */
     public void storeTokenCacheInSession(HttpServletRequest httpServletRequest, String tokenCache){
        httpServletRequest.getSession().setAttribute(Constants.Session.TOKEN_CACHE, tokenCache);
    }

     /**
      * Set authorized object in session obtained in obtain access token.
      * @param httpRequest
      * @param principal
      */
     public void setSessionPrincipal(HttpServletRequest httpRequest, Object principal) {
        httpRequest.getSession().setAttribute(Constants.Session.PRINCIPAL_SESSION_NAME, principal);
    }

     /**
      * Remove authorized object stored in session.
      * @param httpRequest
      */
     void removePrincipalFromSession(HttpServletRequest httpRequest) {
        httpRequest.getSession().removeAttribute(Constants.Session.PRINCIPAL_SESSION_NAME);
    }

     /**
      * Get authorized object stored in session.
      * @param request
      * @return
      */
    public Object getSessionPrincipal(HttpServletRequest request) {
        Object principalSession = request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME);
        if(principalSession==null){
        	throw new IllegalStateException("Session does not contain principal session name");
        } 
        
        return principalSession;
    }

	public Object getAuthObjectFromSession(HttpServletRequest request) {
		Object authObject = request.getSession().getAttribute(Constants.Session.AUTH_OBJECT);
		if(authObject==null){
        	throw new IllegalStateException("Session does not contain auth object");
        } 
        
        return authObject;
	}
}
