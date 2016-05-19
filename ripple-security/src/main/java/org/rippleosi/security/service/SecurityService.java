package org.rippleosi.security.service;

import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public UserDetails parseAuthToken(final TokenResponse tokenResponse) {
        return new TokenResponseToUserDetailsTransformer().transform(tokenResponse);
    }
}
