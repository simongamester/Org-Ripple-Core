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
package org.rippleosi.security.interceptor.csrf;

import org.pac4j.core.authorization.AuthorizationChecker;
import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.authorization.DefaultAuthorizationChecker;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class StartCsrfInterceptor extends HandlerInterceptorAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(StartCsrfInterceptor.class);

    protected AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    protected Config config;

    protected String authorizerName;

    public StartCsrfInterceptor(final Config config, final String authorizerName) {
        this.config = config;
        this.authorizerName = authorizerName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        final WebContext context = new J2EContext(request, response);
        final Authorizer csrfAuthorizer = config.getAuthorizers().get(authorizerName);
        
        LOGGER.debug("authorizerName: {}", authorizerName);
        if (csrfAuthorizer.isAuthorized(context, null)) {
            LOGGER.debug("grant access");

            return true;
        } else {
            LOGGER.debug("forbidden");
        }

        return false;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getAuthorizerName() {
        return authorizerName;
    }

    public void setAuthorizerName(String authorizerName) {
        this.authorizerName = authorizerName;
    }
}
