package org.rippleosi.security.user.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rippleosi.users.model.UserDetails;
import org.rippleosi.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDetails findActiveUser(final HttpServletRequest request, final HttpServletResponse response) {
        return userService.findUserDetails(request, response);
    }
}
