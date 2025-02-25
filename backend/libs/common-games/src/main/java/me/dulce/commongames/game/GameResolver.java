package me.dulce.commongames.game;

import lombok.Getter;

import me.dulce.commongames.Room;
import me.dulce.commongames.User;
import me.dulce.commongames.gamemessage.GameMessageListener;
import me.dulce.commongames.gamemessage.GameMessengerService;
import me.dulce.commonutils.AnnotationScanner;
import me.dulce.commonutils.ReflectUtils;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameResolver.class);

    private final Map<String, Class<? extends Room>> gameIdToClassMap;
    @Getter private final List<GameListing> gameList;
    private final Map<String, Map<Class<? extends Serializable>, List<Method>>>
            gameIdToMapOfGameMessageTypeToMethod;

    @Autowired
    public GameResolver(AnnotationScanner scanner) {
        Map<String, Map.Entry<Class<? extends Room>, GameType>> gameIdToAnnotationEntry =
                getGameIdToClassMap(scanner.getAllClassesWithAnnotation(GameType.class));
        gameIdToClassMap =
                gameIdToAnnotationEntry.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey, entry -> entry.getValue().getKey()));

        gameList =
                gameIdToAnnotationEntry.values().stream()
                        .map(Map.Entry::getValue)
                        .map(gameType -> new GameListing(gameType.gameId(), gameType.displayName()))
                        .toList();

        gameIdToMapOfGameMessageTypeToMethod =
                gameIdToAnnotationEntry.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry ->
                                                getEventListenerMethods(
                                                        scanner, entry.getValue().getKey())));
    }

    @SuppressWarnings("unchecked cast")
    private Map<String, Map.Entry<Class<? extends Room>, GameType>> getGameIdToClassMap(
            Map<Class<?>, GameType> foundGameTypes) {
        return foundGameTypes.entrySet().stream()
                .filter(
                        classDataEntry -> {
                            boolean isValidClass =
                                    ReflectUtils.isClassChildOfParent(
                                            classDataEntry.getKey(), Room.class);

                            if (!isValidClass) {
                                LOGGER.warn(
                                        "Class {} is annotated with @GameType but it does not"
                                                + " implement Room. Implement Room. Class will be"
                                                + " ignored",
                                        classDataEntry.getKey().getTypeName());
                            }
                            return isValidClass;
                        })
                .collect(
                        Collectors.toMap(
                                classDataEntry -> classDataEntry.getValue().gameId(),
                                classDataEntry -> Map.entry((Class<? extends Room>) classDataEntry.getKey(), classDataEntry.getValue())));
    }

    @SuppressWarnings("unchecked cast")
    private Map<Class<? extends Serializable>, List<Method>> getEventListenerMethods(
            AnnotationScanner annotationScanner, Class<? extends Room> classToExtract) {
        List<Method> methods =
                annotationScanner.getMethodsWithAnnotationFromClass(
                        classToExtract, GameMessageListener.class);
        return methods.stream()
                .filter(
                        method -> {
                            boolean isValid = isValidEventListenerMethod(method);

                            if (!isValid) {
                                LOGGER.warn(
                                        "Method {} in class {} is annotated with"
                                            + " @GameMessageListener but it does not follow format"
                                            + " methodName(User sender, AnyMessengerObject data)",
                                        method.getName(),
                                        classToExtract.getTypeName());
                            }
                            return isValid;
                        })
                .collect(
                        Collectors.groupingBy(
                                method ->
                                        (Class<? extends Serializable>)
                                                method.getParameterTypes()[1],
                                Collectors.mapping(method -> method, Collectors.toList())));
    }

    private boolean isValidEventListenerMethod(Method method) {
        // 2 parameters
        if (method.getParameterCount() != 2) {
            return false;
        }

        Class<?>[] parameters = method.getParameterTypes();

        // First parameter is of type User
        if (parameters[0] == User.class) {
            return false;
        }

        // Second parameter is serializable
        return ReflectUtils.isClassChildOfParent(parameters[1], Serializable.class);
    }

    public Optional<Class<? extends Room>> getClassRoomTypeFromGameId(String gameId) {
        return Optional.ofNullable(gameIdToClassMap.get(gameId));
    }

    public List<Method> getEventListenerMethodFromGameIdAndGameMessageType(
            String gameId, Class<? extends Serializable> gameMessageType) {
        if (!gameIdToMapOfGameMessageTypeToMethod.containsKey(gameId)) {
            return List.of();
        }

        Map<Class<? extends Serializable>, List<Method>> gameMessageMethodMap =
                gameIdToMapOfGameMessageTypeToMethod.get(gameId);
        if (!gameMessageMethodMap.containsKey(gameMessageType)) {
            return List.of();
        }
        return gameMessageMethodMap.get(gameMessageType);
    }

    public Optional<Room> constructRoomFromGameId(
            String gameId,
            UUID roomId,
            int maxUserCount,
            User host,
            String roomName,
            GameMessengerService gameMessengerService) {
        Optional<Class<? extends Room>> roomClassType = getClassRoomTypeFromGameId(gameId);
        if (roomClassType.isEmpty()) {
            return Optional.empty();
        }

        Constructor<? extends Room> roomConstructor =
                ConstructorUtils.getMatchingAccessibleConstructor(
                        roomClassType.get(),
                        UUID.class,
                        int.class,
                        User.class,
                        String.class,
                        GameMessengerService.class);

        Class<?>[] parameterTypes = roomConstructor.getParameterTypes();
        Object[] parametersOrdered = new Object[5];

        for (int index = 0; index < parameterTypes.length; index++) {
            parametersOrdered[index] =
                    parameterTypes[index] == UUID.class
                            ? roomId
                            : parameterTypes[index] == int.class
                                    ? maxUserCount
                                    : parameterTypes[index] == User.class
                                            ? host
                                            : parameterTypes[index] == String.class
                                                    ? roomName
                                                    : gameMessengerService;
        }

        try {
            return Optional.of(roomConstructor.newInstance(parametersOrdered));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
