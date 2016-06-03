/*
 * Copyright 2016 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.security.token.rest;

import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;
import org.rippleosi.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.rippleosi.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);
    
    @Autowired
    private SecurityService securityService;
	
    @Autowired
    protected Config config;
    
    @Value("${pac4j.callback.defaultUrl:}")
    protected String defaultUrl;

    @RequestMapping("/token")
    public String loginWithTokens(final HttpServletRequest request, final HttpServletResponse response) {

        final WebContext context = new J2EContext(request, response);
        
        CommonHelper.assertNotNull("config", config);
        final Clients clients = config.getClients();
        CommonHelper.assertNotNull("clients", clients);
        final Client client = clients.findClient("OidcClient");
        LOGGER.debug("client: {}", client);
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");
        
        securityService.setupSecurityContext(context);
        
        return redirectToOriginallyRequestedUrl(context);
    }
	
//	@RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<UserDetails> parseAuthToken(@ModelAttribute TokenResponse tokenResponse) {
//        final UserDetails userDetails = securityService.parseAuthToken(tokenResponse);
//
//        return securityService.redirectToHomePage(userDetails);
//	}
    
    @PostConstruct
    public void postContruct() {
        if (CommonHelper.isBlank(defaultUrl)) {
            this.defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
        }
    }

    protected String redirectToOriginallyRequestedUrl(final WebContext context) {
        final String requestedUrl = (String) context.getSessionAttribute(Pac4jConstants.REQUESTED_URL);
        LOGGER.debug("requestedUrl: {}", requestedUrl);
        final String redirectUrl;
        if (CommonHelper.isNotBlank(requestedUrl)) {
            context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        } else {
            redirectUrl = this.defaultUrl;
        }
        return "redirect:" + redirectUrl;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
