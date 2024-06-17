package me.dulce.gamesite.gamesite2.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.utilservice.GamesiteUtils;
import me.dulce.gamesite.gamesite2.utilservice.SpringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class AuthFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFileService.class);
    private static final String ROOT = "users";
    private static final String UUID_FIELD = "uuid";
    private static final String LOGIN_FIELD = "username";
    private static final String PASSWORD_HASH_FIELD = "passwordHash";

    private final AppConfig appConfig;
    private final SpringService springService;

    private final HashMap<String, UserSecurityDetails> loginToProfileMap;

    @Autowired
    public AuthFileService(
            SpringService springService, AppConfig appConfig, ResourceLoader resourceLoader) {
        this.appConfig = appConfig;
        this.springService = springService;

        loginToProfileMap = new HashMap<>();
        getUserProfiles(resourceLoader);
    }

    public UserSecurityDetails getUser(String username) {
        return loginToProfileMap.get(username.toLowerCase());
    }

    /** Saves user profiles into memory */
    private void getUserProfiles(ResourceLoader resourceLoader) {
        try {
            Resource resource = resourceLoader.getResource(appConfig.getUsersFile());
            File file = resource.getFile();
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
        if (!arrayNode.isArray()) {
            return;
        }
        for (final JsonNode jsonNode : arrayNode) {
            saveProfileFromNode(jsonNode);
        }
        LOGGER.info("Saved {} user profiles to memory", arrayNode.size());
    }

    private void saveProfileFromNode(JsonNode node) {
        if (!node.has(UUID_FIELD) || !node.has(LOGIN_FIELD) || !node.has(PASSWORD_HASH_FIELD)) {
            return;
        }
        String uuidString = node.get(UUID_FIELD).asText();
        String username = node.get(LOGIN_FIELD).asText().toLowerCase();
        String passwordHash = node.get(PASSWORD_HASH_FIELD).asText();

        Optional<UUID> uuidResult = GamesiteUtils.getUUIDFromString(uuidString);
        if (uuidResult.isEmpty()) {
            return;
        }

        UserSecurityDetails userSecurityDetails =
                new UserSecurityDetails(passwordHash, uuidString, uuidResult.get());

        loginToProfileMap.put(username, userSecurityDetails);
    }
}
