package com.example.multitenant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantAwareFilter extends OncePerRequestFilter {
    private final TenantRequestContext tenantRequestContext;

    public TenantAwareFilter(TenantRequestContext tenantRequestContext) {
        this.tenantRequestContext = tenantRequestContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tenantId = request.getIntHeader("Tenant-Id");
        tenantRequestContext.setTenantId(tenantId);
        if (tenantId == -1) {
            response.setStatus(400);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
