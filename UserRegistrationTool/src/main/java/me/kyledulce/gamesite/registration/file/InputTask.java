package me.kyledulce.gamesite.registration.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.kyledulce.gamesite.registration.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class InputTask {

    private static final String INPUT_FIELD = "users";

    private AppConfig appConfig;

    @Autowired
    public InputTask(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public List<UserInput> getData() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(appConfig.getInputFile().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonNode node = jsonNode.get(INPUT_FIELD);
        assert node.isArray();

        ArrayNode arrayNode = (ArrayNode) node;
        LinkedList<UserInput> list = new LinkedList<>();

        for(JsonNode itemNode : arrayNode) {
            list.add(objectMapper.convertValue(itemNode, UserInput.class));
        }
        return list;
    }
}
