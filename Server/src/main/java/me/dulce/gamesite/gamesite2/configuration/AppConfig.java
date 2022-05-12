package me.dulce.gamesite.gamesite2.configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    @Value("${server.allowedOrigins}")
    private List<String> allowedOrigins;

    @Value("${endpoints.socket}")
    private String socketEndpoint;

    @Value("${endpoints.stomp}")
    private String stompEndpoint;

    @Value("${endpoints.frontendPrefix}")
    private String frontendPrefixEndpoint;

    @Value("${database.leaderboardName}")
    private String leaderboardName;

    @Value("${database.gameHist}")
    private String gameHist;

    @Bean
    public DynamoDBMapper dynamoDBMapper(){

        AmazonDynamoDB dbClient = AmazonDynamoDBClient.builder()
                .withRegion(Regions.US_EAST_1)
                .build();

        return new DynamoDBMapper(dbClient);
    }


    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public String[] getAllowedOriginsAsArray() {
        return allowedOrigins.toArray(new String[allowedOrigins.size()]);
    }

    public String getSocketEndpoint() {
        return socketEndpoint;
    }

    public String getStompEndpoint() {
        return stompEndpoint;
    }

    public String getFrontendPrefixEndpoint() {
        return frontendPrefixEndpoint;
    }

    public String getLeaderboardName() {
        return leaderboardName;
    }

    public String getGameHist() {
        return gameHist;
    }
}
