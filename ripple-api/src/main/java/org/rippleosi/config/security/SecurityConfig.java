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

import org.pac4j.core.config.Config;
import org.rippleosi.security.interceptor.authentication.RequiresAuthenticationInterceptor;
import org.rippleosi.security.interceptor.csrf.StartCsrfInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("org.pac4j.springframework.web")
public class SecurityConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Config config;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new StartCsrfInterceptor(config, "csrfAngularToken"))
                .addPathPatterns("/token");

        registry.addInterceptor(new RequiresAuthenticationInterceptor(config, "OidcClient", "all"))
                .addPathPatterns("/user");

        registry.addInterceptor(new RequiresAuthenticationInterceptor(config, "OidcClient", "clinician")) //,csrfAngular"))
                .addPathPatterns("/**")
                .excludePathPatterns("/token", "/user", "/logout", "/patients/9999999000/**");

        registry.addInterceptor(new RequiresAuthenticationInterceptor(config, "OidcClient", "all")) //,csrfAngular"))
                .addPathPatterns("/patients/9999999000/**");

        /* Swagger is uslesss unless logged in, so commenting out the paths that would make swagger available without authentication
                "/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**", "/configuration/**"
        */
    }
}
