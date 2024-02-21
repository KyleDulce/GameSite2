package me.kyledulce.gamesite.registration.file;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.kyledulce.gamesite.registration.security.UserSecurityDetails;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class OutputTask {

    private AppConfig appConfig;

    @Autowired
    public OutputTask(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    List<UserSecurityDetails> userSecurityDetails = new LinkedList<>();

    public void addUser(UserSecurityDetails userSecurityDetails) {
        this.userSecurityDetails.add(userSecurityDetails);
    }

    public void flush() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for(UserSecurityDetails details : userSecurityDetails) {
            arrayNode.addPOJO(details);
        }

        ObjectNode outerObject = objectMapper.createObjectNode();
        outerObject.putIfAbsent("users", arrayNode);
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        try {
            objectWriter.writeValue(new File(appConfig.getOutputDirectory()), outerObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
