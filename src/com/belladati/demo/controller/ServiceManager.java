package com.belladati.demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.belladati.sdk.BellaDati;
import com.belladati.sdk.BellaDatiConnection;
import com.belladati.sdk.BellaDatiService;
import com.belladati.sdk.auth.OAuthRequest;
import com.belladati.sdk.exception.auth.AuthorizationException;

/**
 * Provides access to the BellaDati service stored in the current session.
 * 
 * @author Chris Hennigfeld
 */
@Component
public class ServiceManager {

	@Autowired
	private HttpSession session;

	/** Connection used to contact BellaDati cloud. */
	private final BellaDatiConnection connection = BellaDati.connect();

	/**
	 * Returns <tt>true</tt> if the user is logged in.
	 * 
	 * @return <tt>true</tt> if the user is logged in
	 */
	public boolean isLoggedIn() {
		return getService() != null;
	}

	/**
	 * Returns the service object used to access BellaDati.
	 * 
	 * @return the service object used to access BellaDati, or <tt>null</tt> if
	 *         the user is not logged in
	 */
	public BellaDatiService getService() {
		return (BellaDatiService) session.getAttribute("service");
	}

	/**
	 * Logs out.
	 */
	public void logout() {
		// since there's no session on the BD server,
		// we just need to discard the service object
		storeService(null);
	}

	/**
	 * Stores the given service object in the session. Call with <tt>null</tt>
	 * to clear the service.
	 * 
	 * @param service service object to store
	 */
	private void storeService(BellaDatiService service) {
		session.setAttribute("service", service);
	}

	/**
	 * Initiates OAuth authentication to the BellaDati cloud server with the
	 * given key and secret. Call {@link OAuthRequest#getAuthorizationUrl()} to
	 * point the user to the URL to authorize the request, then complete
	 * authorization by calling {@link #completeOAuth()}.
	 * 
	 * @param key OAuth key set in the domain settings
	 * @param secret OAuth secret set in the domain settings
	 * @return the pending OAuth request
	 */
	public OAuthRequest initiateOAuth(String key, String secret) {
		OAuthRequest request = connection.oAuth(key, secret);
		session.setAttribute("pendingOAuth", request);
		return request;
	}

	/**
	 * Completes authorization of a pending OAuth request and returns the
	 * service object to access BellaDati. Does nothing if no OAuth request is
	 * pending for the current session.
	 * 
	 * @return the service object to access BellaDati, or <tt>null</tt> if no
	 *         OAuth request was pending
	 * @throws AuthorizationException if an error occurred during authorization
	 */
	public BellaDatiService completeOAuth() throws AuthorizationException {
		OAuthRequest request = (OAuthRequest) session.getAttribute("pendingOAuth");
		if (request != null) {
			BellaDatiService service = request.requestAccess();
			storeService(service);
			return service;
		}
		return null;
	}
}
