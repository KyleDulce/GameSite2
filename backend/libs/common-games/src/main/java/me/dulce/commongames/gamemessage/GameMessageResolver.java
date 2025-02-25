package me.dulce.commongames.gamemessage;

import me.dulce.commonutils.AnnotationScanner;
import me.dulce.commonutils.ReflectUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameMessageResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMessageResolver.class);

    private final Map<String, Class<? extends Serializable>> incomingMessageIdToClass;

    @Autowired
    public GameMessageResolver(AnnotationScanner annotationScanner) {
        incomingMessageIdToClass =
                getIncomingMessageIdToClassMap(
                        annotationScanner.getAllClassesWithAnnotation(IncomingGameData.class));
    }

    public Optional<Class<? extends Serializable>> resolveGameMessage(
            GameSerializableMessage gameSerializableMessage) {
        return Optional.ofNullable(
                incomingMessageIdToClass.get(gameSerializableMessage.gameDataIdString));
    }

    @SuppressWarnings("unchecked cast")
    private Map<String, Class<? extends Serializable>> getIncomingMessageIdToClassMap(
            Map<Class<?>, IncomingGameData> foundIncomingGameDataClasses) {
        return foundIncomingGameDataClasses.entrySet().stream()
                .filter(
                        classDataEntry -> {
                            boolean isValidClass =
                                    ReflectUtils.isClassChildOfParent(
                                            classDataEntry.getKey(), Serializable.class);

                            if (!isValidClass) {
                                LOGGER.warn(
                                        "Class {} is annotated with @IncomingGameData but it does"
                                            + " not implement Serializable. Implement Serializable."
                                            + " Class will be ignored",
                                        classDataEntry.getKey().getTypeName());
                            }
                            return isValidClass;
                        })
                .collect(
                        Collectors.toMap(
                                classDataEntry -> classDataEntry.getValue().value(),
                                classDataEntry ->
                                        (Class<? extends Serializable>) classDataEntry.getKey()));
    }
}
