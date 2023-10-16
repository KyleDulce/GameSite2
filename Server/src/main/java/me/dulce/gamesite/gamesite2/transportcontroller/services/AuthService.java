package me.dulce.gamesite.gamesite2.transportcontroller.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.utilservice.SpringService;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.TestOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Service handling authentication of user
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private static final String ROOT = "users";
    private static final String UUID_FIELD = "uuid";
    private static final String LOGIN_HASH_FIELD = "loginHash";
    private static final String PASSWORD_HASH_FIELD = "passwordHash";
    private static final String PASSWORD_SALT_1_FIELD = "passwordSalt1";
    private static final String PASSWORD_SALT_2_FIELD = "passwordSalt2";

    private final AppConfig appConfig;
    private final SpringService springService;

    private final HashMap<String, AuthProfile> loginToProfileMap;

    @Autowired
    public AuthService(
            SpringService springService,
            AppConfig appConfig,
            ResourceLoader resourceLoader) {
        this.appConfig = appConfig;
        this.springService = springService;

        loginToProfileMap = new HashMap<>();
        getUserProfiles(resourceLoader);
    }

    /**
     * Validates credentials
     * @param login login string
     * @param passwordHash password hash from api
     * @return optional with uuid if validated, empty otherwise
     */
    public Optional<UUID> validateAuthCreds(String login, String passwordHash) {
        String loweredLogin = login.toLowerCase();
        if(GamesiteUtils.isBlank(loweredLogin) || GamesiteUtils.isBlank(passwordHash)) {
            return Optional.empty();
        }

        String loginHash = DigestUtils.sha256Hex(loweredLogin);
        if(!loginToProfileMap.containsKey(loginHash)) {
            return Optional.empty();
        }

        AuthProfile authProfile = loginToProfileMap.get(loginHash);
        String completedPasswordBuilder = authProfile.passwordSalt1 +
                passwordHash +
                authProfile.passwordSalt2;

        String finalHash = DigestUtils.sha256Hex(completedPasswordBuilder);
        if(finalHash.equals(authProfile.passwordHash)) {
            return Optional.of(authProfile.uuid);
        }
        return Optional.empty();
    }

    /**
     * Saves user profiles into memory
     */
    private void getUserProfiles(ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource(appConfig.getUsersFile());
            File file = resource.getFile();
            LOGGER.info("Reading file at: {}", file.getAbsolutePath());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode usersTree = mapper.readTree(file);
            storeProfiles(usersTree);
        } catch (Exception e) {
            LOGGER.error("FATAL: Could not get users profile file!", e);
            springService.springAppExit(100);
        }
    }

    private void storeProfiles(JsonNode usersTree) {
        JsonNode arrayNode = usersTree.get(ROOT);
        if(!arrayNode.isArray()) {
            return;
        }
        for(final JsonNode jsonNode : arrayNode) {
            saveProfileFromNode(jsonNode);
        }
        LOGGER.info("Saved user profiles to memory");
    }

    private void saveProfileFromNode(JsonNode node) {
        if(!node.has(UUID_FIELD)
                || !node.has(LOGIN_HASH_FIELD)
                || !node.has(PASSWORD_HASH_FIELD)
                || !node.has(PASSWORD_SALT_1_FIELD)
                || !node.has(PASSWORD_SALT_2_FIELD)) {
            return;
        }
        String uuidString = node.get(UUID_FIELD).asText();
        String loginHash = node.get(LOGIN_HASH_FIELD).asText();
        String passwordHash = node.get(PASSWORD_HASH_FIELD).asText();
        long passwordSalt1 = node.get(PASSWORD_SALT_1_FIELD).asLong();
        long passwordSalt2 = node.get(PASSWORD_SALT_2_FIELD).asLong();

        Optional<UUID> uuidResult = GamesiteUtils.getUUIDFromString(uuidString);
        if(uuidResult.isEmpty()) {
            return;
        }
        AuthProfile result = new AuthProfile();
        result.uuid = uuidResult.get();
        result.loginHash = loginHash;
        result.passwordHash = passwordHash;
        result.passwordSalt1 = passwordSalt1;
        result.passwordSalt2 = passwordSalt2;

        loginToProfileMap.put(loginHash, result);
    }

    @TestOnly
    HashMap<String, AuthProfile> getLoginToProfileMap() {
        return loginToProfileMap;
    }

    private static class AuthProfile {
        UUID uuid;
        String loginHash;
        String passwordHash;
        long passwordSalt1;
        long passwordSalt2;
    }
}
