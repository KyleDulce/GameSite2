package me.dulce.gamesite.gamesite2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GamesiteUserDetailsService implements UserDetailsService {

    @Autowired private AuthFileService authFileService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("Null Username");
        }

        UserSecurityDetails userSecurityDetails = authFileService.getUser(username);
        if (userSecurityDetails == null) {
            throw new UsernameNotFoundException(username);
        }

        return userSecurityDetails;
    }
}
