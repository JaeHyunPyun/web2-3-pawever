package com.pawever.server.domain.user.enums;

import java.util.Arrays;
import java.util.List;

public enum Role {
    ROLE_ADMIN, ROLE_USER, ROLE_STAFF;

    public static List<Role> getRoles(){
        return Arrays.asList(Role.values());
    }
}
