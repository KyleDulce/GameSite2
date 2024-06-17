package me.dulce.gamesite.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import me.dulce.commongames.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserSecurityDetails implements UserDetails {

    private String password;
    private String username;
    @Getter private UUID userId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User toUser() {
        Optional<User> user = User.getUserFromUUID(userId);
        return user.orElseGet(
                () ->
                        User.createNewUser(
                                userId, "", // TODO
                                null));
    }
}
