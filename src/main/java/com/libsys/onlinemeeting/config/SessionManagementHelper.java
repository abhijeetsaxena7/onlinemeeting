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
 * Helpers for managing session
 */
@Component
public class SessionManagementHelper {

     public StateData validateState(HttpSession session, String state) throws Exception {
        if (StringUtils.isNotEmpty(state)) {
            StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new Exception(Messages.FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

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

    public  void storeStateAndNonceInSession(HttpSession session, String state, String nonce) {

        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        if (session.getAttribute(Constants.Session.STATES) == null) {
            session.setAttribute(Constants.Session.STATES, new HashMap<String, StateData>());
        }
        ((Map<String, StateData>) session.getAttribute(Constants.Session.STATES)).put(state, new StateData(nonce, new Date()));
    }

     public void storeTokenCacheInSession(HttpServletRequest httpServletRequest, String tokenCache){
        httpServletRequest.getSession().setAttribute(Constants.Session.TOKEN_CACHE, tokenCache);
    }

     public void setSessionPrincipal(HttpServletRequest httpRequest, Object principal) {
        httpRequest.getSession().setAttribute(Constants.Session.PRINCIPAL_SESSION_NAME, principal);
    }

     void removePrincipalFromSession(HttpServletRequest httpRequest) {
        httpRequest.getSession().removeAttribute(Constants.Session.PRINCIPAL_SESSION_NAME);
    }

    public Object getAuthSessionObject(HttpServletRequest request) {
        Object principalSession = request.getSession().getAttribute(Constants.Session.PRINCIPAL_SESSION_NAME);
        if(principalSession==null){
        	throw new IllegalStateException("Session does not contain principal session name");
        } 
        
        return principalSession;
    }
}
