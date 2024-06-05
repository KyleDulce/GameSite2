package me.dulce.gamesite.gamesite2.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class SecurityContextFactory implements WithSecurityContextFactory<WithSecurityUser> {

    @Autowired private UserDetailsService userDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithSecurityUser withMockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails details = userDetailsService.loadUserByUsername(withMockUser.value());

        // Set the authentication in the security context
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        details, "password", details.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
