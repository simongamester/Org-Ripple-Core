package org.rippleosi.security.service;

import org.rippleosi.security.model.TokenResponse;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public void parseAuthToken(final TokenResponse tokenResponse) {
        final String rawAccessToken = tokenResponse.getAccess_token();
        final String rawIdToken = tokenResponse.getId_token();

        final Jwt accessToken = JwtHelper.decode(rawAccessToken);
        final Jwt idToken = JwtHelper.decode(rawIdToken);
    }
}
