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
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import java.text.ParseException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.profile.OidcProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    @Value("${ripple.homepage.url}")
    private String rippleHomepage;

//    public UserDetails parseAuthToken(final TokenResponse tokenResponse) {
//        return new TokenResponseToUserDetailsTransformer().transform(tokenResponse);
//    }

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
    
    public void setupSecurityContext(final WebContext context) {
        
        final String rawAccessToken = context.getRequestParameter("access_token");
        final String rawIDToken = context.getRequestParameter("id_token");
        final String tokenScope = context.getRequestParameter("scope");
        final String tokenExpiry = context.getRequestParameter("expires_in");
        final Jwt accessToken = JwtHelper.decode(rawAccessToken);
        final Jwt idToken = JwtHelper.decode(rawIDToken);
        
        BearerAccessToken bearerAccessToken = new BearerAccessToken(rawAccessToken, Long.parseLong(tokenExpiry), Scope.parse(tokenScope));
        OidcProfile profile = new OidcProfile(bearerAccessToken);
        profile.setIdTokenString(rawIDToken);
        
        String idClaims = idToken.getClaims();
        String accessClaims = accessToken.getClaims();
        try {
            JWTClaimsSet idClaimsSet = JWTClaimsSet.parse(idClaims);
            UserInfo userInfo = new UserInfo(idClaimsSet);
            profile.addAttributes(userInfo.toJWTClaimsSet().getClaims());
            JWTClaimsSet accessClaimsSet = JWTClaimsSet.parse(accessClaims);
            profile.addRole(accessClaimsSet.getStringClaim("role"));
            LOGGER.debug("profile: {}", profile);
            saveUserProfile(context, profile);
        } catch (ParseException pe) {
            LOGGER.error("Could not parse id claims raw json", pe);
        } catch (com.nimbusds.oauth2.sdk.ParseException pe) {
            LOGGER.error("Could not parse id claims set into attribute map", pe);
        }
    }
    
    protected void saveUserProfile(final WebContext context, final UserProfile profile) {
        final ProfileManager manager = new ProfileManager(context);
        if (profile != null) {
            manager.save(true, profile);
        }
    }
}
