package org.rippleosi.security.service;

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
            profile.addAttribute("tenant", accessClaimsSet.getClaim("tenant"));
            profile.addAttribute("nhs_number", accessClaimsSet.getClaim("nhs_number"));

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
