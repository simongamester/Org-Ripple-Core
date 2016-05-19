package org.rippleosi.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.Transformer;
import org.rippleosi.security.common.util.JsonUtils;
import org.rippleosi.security.model.Claims;
import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

public class TokenResponseToUserDetailsTransformer implements Transformer<TokenResponse, UserDetails> {

    @Override
    public UserDetails transform(final TokenResponse tokenResponse) {
        final String rawAccessToken = tokenResponse.getAccess_token();
        final String rawIdToken = tokenResponse.getId_token();

        final Jwt accessToken = JwtHelper.decode(rawAccessToken);
        final Jwt idToken = JwtHelper.decode(rawIdToken);

        final JsonNode accessTokenClaims = JsonUtils.extractJsonFromString(accessToken.getClaims());
        final JsonNode idTokenClaims = JsonUtils.extractJsonFromString(idToken.getClaims());

        // TODO - need to be set in the ID server
        final Claims claims = new Claims();
        claims.setHomeView("");
        claims.setAutoAdvancedSearch(false);

        final UserDetails userDetails = new UserDetails();
        userDetails.setAccessToken(rawAccessToken);
        userDetails.setSub(accessTokenClaims.get("sub").asText());
        userDetails.setGivenName(idTokenClaims.get("given_name").asText());
        userDetails.setFamilyName(idTokenClaims.get("family_name").asText());
        userDetails.setEmail(idTokenClaims.get("email").asText());
        userDetails.setRole(accessTokenClaims.get("role").asText());
        userDetails.setTenant(accessTokenClaims.get("tenant").asText());
        userDetails.setNhsNumber(accessTokenClaims.get("nhs_number").asText());
        userDetails.setClaims(claims);

        return userDetails;
    }
}
