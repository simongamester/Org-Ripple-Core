package org.rippleosi.users.service;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.profile.UserProfile;
import org.rippleosi.users.model.UserDetails;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertNotNull;

public class UserServiceTest {

    private final String ID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01" +
        "NUZCN0VFMDI3Q0UiLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiU21pdGgiLCJlbWFpbCI6ImpvaG4uc21pdGhAbmhzLmd" +
        "vdi51ayIsImVtYWlsX3ZlcmlmaWVkIjoidHJ1ZSIsImV4cCI6MTQ2MzY3MzIzM30.RqlR3KFgxTgERllenBUyZlpMYd3wiMI4EfBjHQqBNio";

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiJUZXN0LUNsaWVudCIsInN" +
        "jb3BlIjpbInRlc3RTY29wZSJdLCJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01NUZCN0VFMDI3Q0UiLCJ0ZW5hbnQiOiJUZXN0LVR" +
        "lbmFudCIsInJvbGUiOiJUZXN0LVJvbGUiLCJuaHNfbnVtYmVyIjoiOTk5OTk5OTAwMCIsImV4cCI6MTQ2MzY3NjUzM30.2T2auUynL2RcFaH" +
        "9W5iONV1wZSRI784QCOFOQU6WOP4";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserProfile userProfile;

    private UserService userService;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest("GET", "/test");
        request.setParameter("access_token", ACCESS_TOKEN);
        request.setParameter("id_token", ID_TOKEN);
        request.setParameter("scope", "test");
        request.setParameter("expires_in", "3600");

        response = new MockHttpServletResponse();

        userProfile = new UserProfile();
        userProfile.addAttribute("sub", "1234567890");
        userProfile.addAttribute("preferred_username", "test.user");
        userProfile.addAttribute("given_name", "Test");
        userProfile.addAttribute("family_name", "User");
        userProfile.addAttribute("email", "test.user@email.com");
        userProfile.addAttribute("tenant", "Test Tenant");
        userProfile.addAttribute("nhs_number", "1234567890");
        userProfile.addRole("TEST_ROLE");

        userService = new UserService();

    }

    @Test
    public void mustBeAbleToRetrieveActiveUserProfileForTheSession() {
        request.setAttribute(Pac4jConstants.USER_PROFILE, userProfile);

        final UserDetails userDetails = userService.findUserDetails(request, response);

        assertNotNull("Active UserProfile could not be found.", userDetails);
    }
}
