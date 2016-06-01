package org.rippleosi.security.token.rest;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.ParseException;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.profile.OidcProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

//    @Autowired
//    private SecurityService securityService;
//
//    @RequestMapping(method = RequestMethod.POST)
//    public void parseAuthToken(@ModelAttribute TokenResponse tokenResponse) {
//        securityService.parseAuthToken(tokenResponse);
//    }
    
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${pac4j.callback.defaultUrl:}")
    protected String defaultUrl;

    @Autowired
    protected Config config;

    @RequestMapping("/token")
    public String callback(final HttpServletRequest request, final HttpServletResponse response) {

        final WebContext context = new J2EContext(request, response);

        CommonHelper.assertNotNull("config", config);
        final Clients clients = config.getClients();
        CommonHelper.assertNotNull("clients", clients);
        final Client client = clients.findClient("OidcClient");
        LOGGER.debug("client: {}", client);
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");
        
        final String rawAccessToken = request.getParameter("access_token");
        final String rawIDToken = request.getParameter("id_token");
        final String tokenScope = request.getParameter("scope");
        final String tokenExpiry = request.getParameter("expires_in");
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
        
        return redirectToOriginallyRequestedUrl(context);
    }

    @PostConstruct
    public void postContruct() {
        if (CommonHelper.isBlank(defaultUrl)) {
            this.defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
        }
    }
    protected void saveUserProfile(final WebContext context, final UserProfile profile) {
        final ProfileManager manager = new ProfileManager(context);
        if (profile != null) {
            manager.save(true, profile);
        }
    }

    protected String redirectToOriginallyRequestedUrl(final WebContext context) {
        final String requestedUrl = (String) context.getSessionAttribute(Pac4jConstants.REQUESTED_URL);
        LOGGER.debug("requestedUrl: {}", requestedUrl);
        final String redirectUrl;
        if (CommonHelper.isNotBlank(requestedUrl)) {
            context.setSessionAttribute(Pac4jConstants.REQUESTED_URL, null);
            redirectUrl = requestedUrl;
        } else {
            redirectUrl = this.defaultUrl;
        }
        return "redirect:" + redirectUrl;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
