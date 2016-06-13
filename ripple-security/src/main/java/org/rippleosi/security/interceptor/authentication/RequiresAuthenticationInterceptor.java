package org.rippleosi.security.interceptor.authentication;

import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.springframework.http.HttpStatus;

public class RequiresAuthenticationInterceptor extends org.pac4j.springframework.web.RequiresAuthenticationInterceptor {

    public RequiresAuthenticationInterceptor(final Config config) {
        super(config);
    }

    public RequiresAuthenticationInterceptor(final Config config, final String clientName) {
        super(config, clientName);
    }

    public RequiresAuthenticationInterceptor(final Config config, final String clientName, final String authorizerName) {
        super(config, clientName, authorizerName);
    }

    @Override
    protected void redirectToIdentityProvider(final WebContext context, final List<Client> currentClients) {
        super.redirectToIdentityProvider(context, currentClients);

        context.setResponseStatus(HttpStatus.OK.value());
    }
}
