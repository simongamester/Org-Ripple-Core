package org.rippleosi.security.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.profile.OidcProfile;
import org.rippleosi.users.model.UserPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    public void setupSecurityContext(final WebContext context) {

        final String rawAccessToken = context.getRequestParameter("access_token");
        final String rawIDToken = context.getRequestParameter("id_token");
        final String tokenScope = context.getRequestParameter("scope");
        final String tokenExpiry = context.getRequestParameter("expires_in");

        final Jwt accessToken = JwtHelper.decode(rawAccessToken);
        final Jwt idToken = JwtHelper.decode(rawIDToken);

        final BearerAccessToken bearerAccessToken = new BearerAccessToken(rawAccessToken, Long.parseLong(tokenExpiry), Scope.parse(tokenScope));

        final OidcProfile profile = new OidcProfile(bearerAccessToken);
        profile.setIdTokenString(rawIDToken);

        final String idClaims = idToken.getClaims();
        final String accessClaims = accessToken.getClaims();

        try {
            final JWTClaimsSet idClaimsSet = JWTClaimsSet.parse(idClaims);

            final UserInfo userInfo = new UserInfo(idClaimsSet);
            profile.addAttributes(userInfo.toJWTClaimsSet().getClaims());

            final JWTClaimsSet accessClaimsSet = JWTClaimsSet.parse(accessClaims);
            final String role = accessClaimsSet.getStringClaim("role");
            profile.addRole(role);
            profile.addAttribute("tenant", accessClaimsSet.getClaim("tenant"));
            profile.addAttribute("nhs_number", accessClaimsSet.getClaim("nhs_number"));

            final UserPermissions userPermissions = new UserPermissions(role);
            profile.addPermissions(userPermissions.loadUserPermissions());

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

    public ResponseEntity<String> generateRedirectResponseEntity(final String defaultUrl, final Map<String, String> params,
                                                                 final HttpStatus httpStatus) {
        CommonHelper.assertNotNull("defaultUrl", defaultUrl);
        CommonHelper.assertNotBlank("defaultUrl", defaultUrl);

        final String redirectUrl = getExpandedUrl(defaultUrl, params);
        final HttpHeaders httpHeaders = new HttpHeaders();

        URI redirectPage;

        try {
            redirectPage = new URI(redirectUrl);
            httpHeaders.setLocation(redirectPage);
        }
        catch (final URISyntaxException e) {
            LOGGER.warn("The security service has failed to redirect to the requested URL.");
            LOGGER.debug("The security service has failed to redirect to the requested URL.", e);
        }

        return new ResponseEntity<>(httpHeaders, httpStatus);
    }

    private String getExpandedUrl(final String defaultUrl, final Map<String, String> params) {
        if (params == null) {
            return defaultUrl;
        }

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(defaultUrl);

        for (final Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return builder.toUriString();
    }
}
