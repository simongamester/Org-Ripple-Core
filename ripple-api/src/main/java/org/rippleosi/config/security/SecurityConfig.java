package org.rippleosi.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
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
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .authorizeRequests()
                .antMatchers("/api/swagger-ui.html").permitAll()
                .antMatchers("/api/token").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/**").authenticated()
                .and()
            .openidLogin()
                .loginPage(authenticationServerUrl())
                .and()
            .csrf()
                .csrfTokenRepository(csrfTokenRepository())
                    .and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
        // @formatter:on
    }

    private CsrfTokenRepository csrfTokenRepository() {
        final HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");

        return repository;
    }

    private String authenticationServerUrl() {
        final UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(authenticationServerUrl + "/identity/connect/authorize")
            .queryParam("client_id", clientId)
            .queryParam("scope", "openid profile email api")
            .queryParam("response_type", "id_token token")
            .queryParam("redirect_uri", redirectUri)
            .queryParam("nonce", "nonce");

        return builder.toUriString();
    }
}
