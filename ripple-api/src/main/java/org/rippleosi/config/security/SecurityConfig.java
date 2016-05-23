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
package org.rippleosi.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${authentication.server.url}")
    private String authenticationServerUrl;

    @Value("${authentication.client.id}")
    private String clientId;

    @Value("${authentication.redirect.uri}")
    private String redirectUri;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
            .authorizeRequests()
                .antMatchers("/api/swagger-ui.html").permitAll()
                .antMatchers("/api/token").access("anonymous")
                .antMatchers("/api/**").authenticated()
                .antMatchers("/**").authenticated()
                .and()
            .openidLogin()
                .loginPage(authenticationServerUrl())
                .and()
            .csrf()
                .disable();
        // @formatter:on
    }

    private String authenticationServerUrl() {
        final UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(authenticationServerUrl + "/identity/connect/authorize")
            .queryParam("client_id", clientId)
            .queryParam("scope", "openid profile email api")
            .queryParam("response_type", "id_token token")
            .queryParam("redirect_uri", redirectUri)
            .queryParam("nonce", "nonce")
            .queryParam("response_mode", "form_post");

        return builder.toUriString();
    }
}
