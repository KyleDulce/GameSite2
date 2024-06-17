package me.dulce.commongames.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameResolver {

    private final Map<String, GameServiceManager> gameServiceManagerMap;

    @Autowired
    public GameResolver(GameServiceManager[] gameServiceManagers) {
        this.gameServiceManagerMap =
                Arrays.stream(gameServiceManagers)
                        .collect(Collectors.toMap(GameServiceManager::getGameId, item -> item));
    }

    public Optional<GameServiceManager> getGameServiceManagerFromId(String gameId) {
        return Optional.of(gameServiceManagerMap.get(gameId));
    }

    public List<GameListing> getGameList() {
        return gameServiceManagerMap.values().stream()
                .map(
                        gameServiceManager ->
                                new GameListing(
                                        gameServiceManager.getGameId(),
                                        gameServiceManager.getGameDisplayName()))
                .toList();
    }
}
