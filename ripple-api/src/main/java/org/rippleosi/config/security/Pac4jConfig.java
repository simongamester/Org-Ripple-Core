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
package org.rippleosi.config.security;

import org.pac4j.core.authorization.RequireAllRolesAuthorizer;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.oidc.client.OidcClient;
import org.rippleosi.security.authorizer.csrf.CsrfAngularTokenGeneratorAuthorizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
public class Pac4jConfig {

    @Value("${authentication.server.url}")
    private String authenticationServerUrl;

    @Value("${authentication.client.id}")
    private String clientId;

    @Value("${authentication.redirect.uri}")
    private String redirectUri;
    
    @Bean
    public Config config() {
        final OidcClient oidcClient = new OidcClient();
        oidcClient.setClientID(clientId);
        oidcClient.setSecret("secret");
        oidcClient.setDiscoveryURI(authenticationServerUrl + "/identity/.well-known/openid-configuration");
        oidcClient.addCustomParam("client_id", clientId);
        oidcClient.addCustomParam("scope", "openid profile email api");
        oidcClient.addCustomParam("response_type", "id_token token");
        oidcClient.addCustomParam("redirect_uri", redirectUri);
        oidcClient.addCustomParam("nonce", "nonce");
        oidcClient.addCustomParam("response_mode", "form_post");
        
        final Clients clients = new Clients(redirectUri, oidcClient);

        final CsrfAuthorizer angularCsrfAuthorizer = new CsrfAuthorizer("X-XSRF-TOKEN","X-XSRF-TOKEN");
        angularCsrfAuthorizer.setOnlyCheckPostRequest(false);
        
        final Config config = new Config(clients);
        config.addAuthorizer("clinician", new RequireAllRolesAuthorizer("IDCR"));
        config.addAuthorizer("patient", new RequireAllRolesAuthorizer("PHR"));
        config.addAuthorizer("all", new RequireAnyRoleAuthorizer("IDCR","PHR"));
        config.addAuthorizer("governance", new RequireAllRolesAuthorizer("IG"));
        config.addAuthorizer("csrfAngularToken", new CsrfAngularTokenGeneratorAuthorizer(new DefaultCsrfTokenGenerator()));
        config.addAuthorizer("csrfAngular", angularCsrfAuthorizer);
        return config;
    }
    
}
