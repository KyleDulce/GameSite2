package me.dulce.gamesite.utilservice;

import me.dulce.commongames.User;
import me.dulce.gamesite.security.UserSecurityDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.Optional;
import java.util.UUID;

/** General Utility class for common methods */
public class UserSecurityUtils {

    public static User getUserSecurityDetailsFromPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            return User.getUserFromUUID(((Jwt) principal).getSubject())
                    .orElseThrow(() -> new JwtException("Invalid JWT"));
        } else if (principal instanceof UserSecurityDetails) {
            return getUserFromSecurityDetails((UserSecurityDetails) principal);
        } else {
            throw new IllegalStateException(
                    "Authentication Principal is neither JWT nor security details");
        }
    }

    public static User getUserFromSecurityDetails(UserSecurityDetails userSecurityDetails) {
        return User.getUserFromUUID(userSecurityDetails.getUserId())
                .orElseGet(() -> User.createNewUser(
                        userSecurityDetails.getUserId(),
                        "", // TODO add
                        null
                ));
    }
}
