package me.dulce.gamesite.gamesite2.testutils;

import java.util.*;
import me.dulce.gamesite.gamesite2.security.UserSecurityDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
public class SpringSecurityTestConfiguration {

    public static final String BASIC_USER_UUID_STR = "e7623437-b3e1-406d-8456-b79c175d153c";
    public static final UUID BASIC_USER_UUID = UUID.fromString(BASIC_USER_UUID_STR);
    public static final UserSecurityDetails BASIC_USER_DETAILS =
            new UserSecurityDetails("{noop}abc", "basicUser", BASIC_USER_UUID);

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        HashMap<String, UserSecurityDetails> users = new HashMap<>();
        users.put("basicUser", BASIC_USER_DETAILS);

        return username -> {
            if (users.containsKey(username)) {
                return users.get(username);
            }
            throw new UsernameNotFoundException(username);
        };
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
