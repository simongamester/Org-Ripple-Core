package org.rippleosi.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${authentication.server.url}")
    private String authenticationServerUrl;

    @Value("${authentication.client.id}")
    private String applicationId;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .and()
            .formLogin()
                .loginPage(authenticationServerUrl + "/login?signin=" + applicationId)
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
}
