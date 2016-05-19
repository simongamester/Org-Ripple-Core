package org.rippleosi.security.service;

import org.junit.Before;
import org.junit.Test;

public class TokenResponseToUserDetailsTransformerTest {

    private TokenResponseToUserDetailsTransformer transformer;

    @Before
    public void setUp() {
        transformer = new TokenResponseToUserDetailsTransformer();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWithNullInputToken() {
        transformer.transform(null);
    }
}
