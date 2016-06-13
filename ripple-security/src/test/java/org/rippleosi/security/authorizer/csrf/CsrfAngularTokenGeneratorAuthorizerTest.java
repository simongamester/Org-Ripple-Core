package org.rippleosi.security.authorizer.csrf;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pac4j.core.authorization.authorizer.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CsrfAngularTokenGeneratorAuthorizerTest {

    private static final String ANGULAR_XSRF_TOKEN = "XSRF-TOKEN";

    private CsrfAngularTokenGeneratorAuthorizer authorizer;
    private WebContext context;
    private UserProfile profile;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest("GET", "/test");
        response = new MockHttpServletResponse();

        context = new J2EContext(request, response);
        profile = Mockito.mock(UserProfile.class);

        authorizer = new CsrfAngularTokenGeneratorAuthorizer(new DefaultCsrfTokenGenerator());
    }

    @Test
    public void shouldSetCsrfTokenRequestAttribute() {
        authorizer.isAuthorized(context, profile);

        assertNotNull("The 'XSRF-TOKEN' request attribute has not been set.", context.getRequestAttribute(ANGULAR_XSRF_TOKEN));
    }

    @Test
    public void shouldSetCsrfCookieInResponse() {
        authorizer.isAuthorized(context, profile);

        final Cookie csrfCookie = ((MockHttpServletResponse) response).getCookie(ANGULAR_XSRF_TOKEN);

        assertNotNull("The 'XSRF-TOKEN' cookie has not been set.", csrfCookie);
    }

    @Test
    public void shouldSetDomainOnCsrfCookieInResponse() {
        authorizer.isAuthorized(context, profile);

        final Cookie csrfCookie = ((MockHttpServletResponse) response).getCookie(ANGULAR_XSRF_TOKEN);

        assertNotNull("The 'XSRF-TOKEN' cookie domain has not been set.", csrfCookie.getDomain());
        assertEquals("The 'XSRF-TOKEN' cookie domain is incorrect.", context.getServerName(), csrfCookie.getDomain());
    }
}
