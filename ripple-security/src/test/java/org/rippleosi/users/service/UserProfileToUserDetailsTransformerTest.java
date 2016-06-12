package org.rippleosi.users.service;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.UserProfile;
import org.rippleosi.users.model.UserDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserProfileToUserDetailsTransformerTest {

    private UserProfile userProfile;
    private UserProfileToUserDetailsTransformer transformer;

    @Before
    public void setUp() {
        userProfile = new UserProfile();

        userProfile.addAttribute("sub", "1234567890");
        userProfile.addAttribute("preferred_username", "test.user");
        userProfile.addAttribute("given_name", "Test");
        userProfile.addAttribute("family_name", "User");
        userProfile.addAttribute("email", "test.user@email.com");
        userProfile.addAttribute("tenant", "Test Tenant");
        userProfile.addAttribute("nhs_number", "1234567890");

        userProfile.addRole("TEST_ROLE");

        transformer = new UserProfileToUserDetailsTransformer();
    }

    @Test
    public void mustReturnUserDetailsObject() {
        final UserDetails userDetails = transformer.transform(userProfile);

        assertNotNull("UserDetails object must not be null", userDetails);
    }

    @Test
    public void mustReturnValidUserDetailsObjectWithValidInputs() {
        final UserDetails userDetails = transformer.transform(userProfile);

        assertEquals("UserDetails 'sub' field was not set.", userProfile.getAttribute("sub"), userDetails.getSub());

        assertEquals("UserDetails 'preferred_username' field was not set.", userProfile.getAttribute("preferred_username"), userDetails.getUsername());

        assertEquals("UserDetails 'given_name' field was not set.", userProfile.getAttribute("given_name"), userDetails.getGivenName());

        assertEquals("UserDetails 'family_name' field was not set.", userProfile.getAttribute("family_name"), userDetails.getFamilyName());

        assertEquals("UserDetails 'email' field was not set.", userProfile.getAttribute("email"), userDetails.getEmail());

        assertEquals("UserDetails 'tenant' field was not set.", userProfile.getAttribute("tenant"), userDetails.getTenant());

        assertEquals("UserDetails 'nhs_number' field was not set.", userProfile.getAttribute("nhs_number"), userDetails.getNhsNumber());

        assertEquals("UserDetails 'role' field was not set.", userProfile.getRoles().get(0), userDetails.getRole());

        assertNotNull("UserDetails 'permissions' field was not set.", userDetails.getPermissions());
        assertTrue("UserDetails 'permissions' field is empty.", userDetails.getPermissions().size() > 0);
    }
}
