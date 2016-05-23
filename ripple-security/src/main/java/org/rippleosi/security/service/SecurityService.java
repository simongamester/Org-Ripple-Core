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
package org.rippleosi.security.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    @Value("${ripple.homepage.url}")
    private String rippleHomepage;

    public UserDetails parseAuthToken(final TokenResponse tokenResponse) {
        return new TokenResponseToUserDetailsTransformer().transform(tokenResponse);
    }

    public ResponseEntity<UserDetails> redirectToHomePage(final UserDetails userDetails) {
        URI home = null;

        try {
            home = new URI(rippleHomepage);
        }
        catch (final URISyntaxException e) {
            LOGGER.warn("The security service has failed to redirect to the Ripple home page after authentication.");
            LOGGER.debug("The security service has failed to redirect to the Ripple home page after authentication.", e);
        }

        final HttpHeaders httpHeaders = new HttpHeaders();

        if (home != null) {
            httpHeaders.setLocation(home);
        }

        return new ResponseEntity<>(userDetails, httpHeaders, HttpStatus.SEE_OTHER);
    }
}
