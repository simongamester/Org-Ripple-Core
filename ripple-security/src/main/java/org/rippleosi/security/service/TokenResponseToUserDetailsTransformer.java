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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.pac4j.core.profile.UserProfile;
import org.rippleosi.security.model.UserDetails;

public class TokenResponseToUserDetailsTransformer implements Transformer<UserProfile, UserDetails> {

    @Override
    public UserDetails transform(final UserProfile userProfile) {

        final Map<String, Object> profileAttributes = userProfile.getAttributes();

        final List<String> claims = new ArrayList<>();
        claims.add("READ");
        claims.add("WRITE");

        final UserDetails userDetails = new UserDetails();
        userDetails.setSub(MapUtils.getString(profileAttributes, "sub"));
        userDetails.setGivenName(MapUtils.getString(profileAttributes, "given_name"));
        userDetails.setFamilyName(MapUtils.getString(profileAttributes, "family_name"));
        userDetails.setEmail(MapUtils.getString(profileAttributes, "email"));
        userDetails.setRole(userProfile.getRoles().get(0));
        userDetails.setTenant(MapUtils.getString(profileAttributes, "tenant"));
        userDetails.setNhsNumber(MapUtils.getString(profileAttributes, "nhs_number"));
        userDetails.setClaims(claims);

        return userDetails;
    }
}
