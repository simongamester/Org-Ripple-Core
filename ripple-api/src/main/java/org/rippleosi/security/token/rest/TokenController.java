package org.rippleosi.security.token.rest;

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
