package org.rippleosi.users.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.rippleosi.users.model.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserDetails findUserDetails(final HttpServletRequest request, final HttpServletResponse response) {
        final WebContext context = new J2EContext(request, response);
        final ProfileManager manager = new ProfileManager(context);

        final UserProfile userProfile = manager.get(true);

        return new UserProfileToUserDetailsTransformer().transform(userProfile);
    }
}
