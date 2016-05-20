package org.rippleosi.security.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.rippleosi.security.model.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {

    private SecurityService securityService;
    private UserDetails userDetails;

    @Before
    public void setUp() {
        securityService = new SecurityService();
        userDetails = new UserDetails();

        ReflectionTestUtils.setField(securityService, "rippleHomepage", "http://localhost:8080/");
    }

    @Test
    public void redirectEntityShouldNotBeNull() {
        final ResponseEntity<UserDetails> redirect = securityService.redirectToHomePage(userDetails);

        assertNotNull("Redirect object cannot be null", redirect);
    }

    @Test
    public void redirectEntityMustHaveA303HttpStatus() {
        final ResponseEntity<UserDetails> redirect = securityService.redirectToHomePage(userDetails);

        assertEquals("Redirect HTTP status must be 303 (See Other).", HttpStatus.SEE_OTHER, redirect.getStatusCode());
    }

    @Test
    public void redirectEntityMustContainUserDetailsObjectInItsBody() {
        final ResponseEntity<UserDetails> redirect = securityService.redirectToHomePage(userDetails);

        assertEquals("Redirect must contain the UserDetails object in its body.", userDetails, redirect.getBody());
    }

    @Test
    public void redirectEntityMustContainRedirectUrlInItsHeaderWithValidInput() {
        final ResponseEntity<UserDetails> redirect = securityService.redirectToHomePage(userDetails);

        final Object expected = ReflectionTestUtils.getField(securityService, "rippleHomepage");
        final String actual = redirect.getHeaders().getLocation().toString();

        assertEquals("Redirect headers must contain a redirect location equal to the value passed in.", expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void redirectEntityMustThrowExceptionWithInvalidInput() {
        ReflectionTestUtils.setField(securityService, "rippleHomepage", null);

        final ResponseEntity<UserDetails> redirect = securityService.redirectToHomePage(userDetails);

        final Object expected = ReflectionTestUtils.getField(securityService, "rippleHomepage");
        final String actual = redirect.getHeaders().getLocation().toString();

        assertEquals("Redirect headers must contain a redirect location equal to the value passed in.", expected, actual);
    }
}
