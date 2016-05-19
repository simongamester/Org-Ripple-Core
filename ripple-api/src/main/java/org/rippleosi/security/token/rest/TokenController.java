package org.rippleosi.security.token.rest;

import org.rippleosi.security.model.TokenResponse;
import org.rippleosi.security.model.UserDetails;
import org.rippleosi.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private SecurityService securityService;

    @RequestMapping(method = RequestMethod.POST)
    public UserDetails parseAuthToken(@ModelAttribute TokenResponse tokenResponse) {
        return securityService.parseAuthToken(tokenResponse);
    }
}
