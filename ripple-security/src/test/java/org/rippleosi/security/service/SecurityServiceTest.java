package org.rippleosi.security.service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SecurityServiceTest {

    private final String ID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01" +
        "NUZCN0VFMDI3Q0UiLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiU21pdGgiLCJlbWFpbCI6ImpvaG4uc21pdGhAbmhzLmd" +
        "vdi51ayIsImVtYWlsX3ZlcmlmaWVkIjoidHJ1ZSIsImV4cCI6MTQ2MzY3MzIzM30.RqlR3KFgxTgERllenBUyZlpMYd3wiMI4EfBjHQqBNio";

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiJUZXN0LUNsaWVudCIsInN" +
        "jb3BlIjpbInRlc3RTY29wZSJdLCJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01NUZCN0VFMDI3Q0UiLCJ0ZW5hbnQiOiJUZXN0LVR" +
        "lbmFudCIsInJvbGUiOiJUZXN0LVJvbGUiLCJuaHNfbnVtYmVyIjoiOTk5OTk5OTAwMCIsImV4cCI6MTQ2MzY3NjUzM30.2T2auUynL2RcFaH" +
        "9W5iONV1wZSRI784QCOFOQU6WOP4";

    private SecurityService securityService;

    @Before
    public void setUp() {
        securityService = new SecurityService();
    }

    private J2EContext setUpWebContext() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.setParameter("access_token", ACCESS_TOKEN);
        request.setParameter("id_token", ID_TOKEN);
        request.setParameter("scope", "test");
        request.setParameter("expires_in", "3600");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        return new J2EContext(request, response);
    }

    @Test
    public void userProfileMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        securityService.setupSecurityContext(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        assertNotNull("The user profile wasn't saved whilst setting up the security context.", userProfile);
    }

    @Test
    public void userIdAttributesMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        securityService.setupSecurityContext(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final Map<String, Object> attributes = userProfile.getAttributes();

        assertNotNull("The user's ID attributes weren't saved whilst setting up the security context.", attributes);
        assertTrue("The user's ID attributes weren't saved whilst setting up the security context.", attributes.size() > 0);
    }

    @Test
    public void userRoleMustBeSavedWithValidTokenInputs() {
        final WebContext context = setUpWebContext();

        securityService.setupSecurityContext(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final List<String> roles = userProfile.getRoles();

        assertNotNull("The user role wasn't saved whilst setting up the security context.", roles);
        assertEquals("The user role saved doesn't correspond to the input value.", "Test-Role", roles.get(0));
    }

    @Test
    public void userProfileMustContainThePermissionsOfTheRole() {
        final WebContext context = setUpWebContext();

        securityService.setupSecurityContext(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final List<String> permissions = userProfile.getPermissions();

        assertNotNull("The user's permissions have not been saved in the user profile.", permissions);
    }

    @Test
    public void userPermissionsMustBeNoneIfInvalidRoleIsUsed() {
        final WebContext context = setUpWebContext();

        securityService.setupSecurityContext(context);

        final ProfileManager manager = new ProfileManager(context);
        final UserProfile userProfile = manager.get(true);

        final List<String> permissions = userProfile.getPermissions();

        assertEquals("If an invalid user role is used, the user cannot be granted permissions.", "NONE", permissions.get(0));
    }

    @Test
    public void shouldNotGenerateLocationHeaderIfRedirectUrlIsMalformed() {
        final String invalidUrl = "Malformed" + "\n" + "URL";

        final ResponseEntity<String> responseEntity =
            securityService.generateRedirectResponseEntity(invalidUrl, null, HttpStatus.I_AM_A_TEAPOT);

        assertNull("The redirect location header must not be set if invalid.", responseEntity.getHeaders().getLocation());
    }

    @Test
    public void shouldExpandRedirectUrlIfParamsIncluded() {
        final String expectedUrl = "http://www.google.co.uk/search?param1=test&param2=test";

        final String inputUrlBasePath = "http://www.google.co.uk/search";

        final Map<String, String> params = new HashMap<>();
        params.put("param1", "test");
        params.put("param2", "test");

        final ResponseEntity<String> responseEntity =
            securityService.generateRedirectResponseEntity(inputUrlBasePath, params, HttpStatus.I_AM_A_TEAPOT);

        final URI outputUrl = responseEntity.getHeaders().getLocation();

        assertNotNull("The redirect location URL must not be null using valid inputs.", outputUrl);
        assertEquals("The redirect location URL must be fully expanded using the given params.", expectedUrl, outputUrl.toString());
    }
}
