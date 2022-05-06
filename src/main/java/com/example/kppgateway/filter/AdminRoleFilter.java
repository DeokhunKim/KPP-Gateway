package com.example.kppgateway.filter;

import org.springframework.stereotype.Component;

@Component
public class AdminRoleFilter extends RoleFilter{
    public AdminRoleFilter() {
        super.filterUserRole = "[ROLE_ADMIN]";
    }
}
