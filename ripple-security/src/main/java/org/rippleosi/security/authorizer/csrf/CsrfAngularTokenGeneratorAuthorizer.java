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
package org.rippleosi.security.authorizer.csrf;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.authorization.authorizer.csrf.CsrfTokenGenerator;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

/**
 *
 * @author ajbrindley
 */
public class CsrfAngularTokenGeneratorAuthorizer implements Authorizer<UserProfile> {

    private static final String ANGULAR_XSRF_TOKEN = "XSRF-TOKEN";
    
    private final CsrfTokenGenerator csrfTokenGenerator;

    public CsrfAngularTokenGeneratorAuthorizer(final CsrfTokenGenerator csrfTokenGenerator) {
        this.csrfTokenGenerator = csrfTokenGenerator;
    }
    
    @Override
    public boolean isAuthorized(WebContext context, UserProfile profile) {
        CommonHelper.assertNotNull("csrfTokenGenerator", csrfTokenGenerator);

        final String token = csrfTokenGenerator.get(context);
        context.setRequestAttribute(ANGULAR_XSRF_TOKEN, token);

        final Cookie cookie = new Cookie(ANGULAR_XSRF_TOKEN, token);
        cookie.setDomain(context.getServerName());
        context.addResponseCookie(cookie);

        return true;
    }
    
}
