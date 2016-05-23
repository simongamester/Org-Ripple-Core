/*
 * Copyright 2016 Ripple OSI
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rippleosi.security.service;

import org.junit.Before;
import org.junit.Test;
import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TokenResponseToUserDetailsTransformerTest {

    private final String ID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01" +
        "NUZCN0VFMDI3Q0UiLCJnaXZlbl9uYW1lIjoiSm9obiIsImZhbWlseV9uYW1lIjoiU21pdGgiLCJlbWFpbCI6ImpvaG4uc21pdGhAbmhzLmd" +
        "vdi51ayIsImVtYWlsX3ZlcmlmaWVkIjoidHJ1ZSIsImV4cCI6MTQ2MzY3MzIzM30.RqlR3KFgxTgERllenBUyZlpMYd3wiMI4EfBjHQqBNio";

    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfaWQiOiJUZXN0LUNsaWVudCIsInN" +
        "jb3BlIjpbInRlc3RTY29wZSJdLCJzdWIiOiIyOEFEODU3Ni0xOTQ4LTRDODQtOEI1RS01NUZCN0VFMDI3Q0UiLCJ0ZW5hbnQiOiJUZXN0LVR" +
        "lbmFudCIsInJvbGUiOiJUZXN0LVJvbGUiLCJuaHNfbnVtYmVyIjoiOTk5OTk5OTAwMCIsImV4cCI6MTQ2MzY3NjUzM30.2T2auUynL2RcFaH" +
        "9W5iONV1wZSRI784QCOFOQU6WOP4";

    private TokenResponseToUserDetailsTransformer transformer;

    @Before
    public void setUp() {
        transformer = new TokenResponseToUserDetailsTransformer();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWithNullInputToken() {
        transformer.transform(null);
    }

    @Test
    public void shouldCreateUserDetailsObjectWithValidTokenInputs() {
        final TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(ACCESS_TOKEN);
        tokenResponse.setId_token(ID_TOKEN);

        final UserDetails userDetails = transformer.transform(tokenResponse);

        assertNotNull("UserDetails object cannot be null with valid input.", userDetails);
        assertNotNull("The user's claims could not be parsed.", userDetails.getClaims());

        assertEquals("Sub could not be parsed.", "28AD8576-1948-4C84-8B5E-55FB7EE027CE", userDetails.getSub());
        assertEquals("The user's given name could not be parsed.", "John", userDetails.getGivenName());
        assertEquals("The user's family name could not be parsed.", "Smith", userDetails.getFamilyName());
        assertEquals("The user's email address could not be parsed.", "john.smith@nhs.gov.uk", userDetails.getEmail());
        assertEquals("The user's role could not be parsed.", "Test-Role", userDetails.getRole());
        assertEquals("The user's nhsNumber could not be parsed.", "9999999000", userDetails.getNhsNumber());
        assertEquals("The system tenant could not be parsed.", "Test-Tenant", userDetails.getTenant());
    }
}
