package com.belladati.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.belladati.sdk.auth.OAuthRequest;

/**
 * Controller handling logic related to authorization.
 * 
 * @author Chris Hennigfeld
 */
@Controller
public class AuthorizationController {

	@Autowired
	private ServiceManager serviceManager;

	@Value("${consumer.key}")
	private String key;

	@Value("${consumer.secret}")
	private String secret;

	/**
	 * Shows the login page.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView showLogin() {
		return new ModelAndView("login");
	}

	/**
	 * Redirects the user to BellaDati for OAuth authorization.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request) {
		// replace key and secret by your domain's key/secret
		OAuthRequest oAuthRequest = serviceManager.initiateOAuth(key, secret);
		return new ModelAndView("redirect:" + oAuthRequest.getAuthorizationUrl() + "?callbackUrl=" + getDeploymentUrl(request)
			+ "/authorize");
	}

	/**
	 * Landing page after OAuth authorization. Completes OAuth.
	 */
	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public ModelAndView getAccessToken() {
		// here we should catch AuthorizationExceptions and show an error
		serviceManager.completeOAuth();
		return new ModelAndView("redirect:/");
	}

	/**
	 * Logs out.
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView doLogout() {
		serviceManager.logout();
		return new ModelAndView("redirect:/");
	}

	/**
	 * Finds the root URL of the current deployment based on the user's request.
	 * 
	 * @param request request from the user
	 * @return the deployment root, including scheme, server, port, and path
	 */
	private String getDeploymentUrl(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		return requestUrl.substring(0, requestUrl.length() - servletPath.length());
	}
}
