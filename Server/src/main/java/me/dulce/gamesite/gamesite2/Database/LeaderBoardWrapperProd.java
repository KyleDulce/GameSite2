package me.dulce.gamesite.gamesite2.Database;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class LeaderBoardWrapperProd {


    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Player save(Player player, DynamoDBSaveExpression id){
        dynamoDBMapper.save(player, new DynamoDBSaveExpression().withExpectedEntry("id", new ExpectedAttributeValue(new AttributeValue().withS(String.valueOf(id)))));
        return player;

    }

    public Player findById(String id){

        return dynamoDBMapper.load(Player.class, id);
    }

    public List<Player> findall(){
        return dynamoDBMapper.scan(Player.class, new DynamoDBScanExpression());
    }

    public String update(String id, Player player ){
        dynamoDBMapper.save(player,
                new DynamoDBSaveExpression().withExpectedEntry("id",
                        new ExpectedAttributeValue(
                                new AttributeValue().withS(id)
                        )));
        return id;
    }

    public String delete(String id){
        dynamoDBMapper.delete(id);
        return "Player " + id + " was removed from the game";
    }

}
