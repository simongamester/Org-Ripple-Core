package org.rippleosi.users.model;

import java.util.ArrayList;
import java.util.List;

public class UserPermissions {

    private String role;

    public UserPermissions(final String role) {
        this.role = role;
    }

    public List<String> loadUserPermissions() {
        final List<String> permissions = new ArrayList<>();
        final String role = getRole();

        if (role.equalsIgnoreCase("IDCR")) {
            permissions.add("READ");
            permissions.add("WRITE");
        }
        else if (role.equalsIgnoreCase("PHR")) {
            permissions.add("READ");
        }
        else if (role.equalsIgnoreCase("ADMIN")) {
            permissions.add("ADMIN");
        }
        else if (role.equalsIgnoreCase("IG")) {
            permissions.add("IG");
        }
        else {
            permissions.add("NONE");
        }

        return addPermissions(permissions);
    }

    protected List<String> addPermissions(final List<String> permissions) {
        return permissions;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
