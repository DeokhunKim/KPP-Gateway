package com.example.kppgateway.filter;

import org.springframework.cloud.gateway.filter.factory.SetRequestHostHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetResponseHeaderGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class UserRoleFilter extends RoleFilter{
    public UserRoleFilter() {
        super.filterUserRole = "[ROLE_USER]";
    }



}
