package io.ihankun.framework.mongoplus.conn;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


/**
 * @author hankun
 * 连接工具类
 * @since 2023-02-09 14:47
 **/
public class ConnectMongoDB {

    private final MongoClient mongoClient;

    private final String database;

    private final String collection;

    private MongoDatabase mongoDatabase;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public String getDatabase() {
        return database;
    }

    public String getCollection() {
        return collection;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public ConnectMongoDB(MongoClient mongoClient, String database, String collectionValue) {
        this.mongoClient = mongoClient;
        this.database = database;
        this.collection = collectionValue;
    }

    public MongoCollection<Document> open(){
        mongoDatabase = mongoClient.getDatabase(database);
        return mongoDatabase.getCollection(collection,Document.class);
    }

    public boolean isSame(String database, String collection) {
        return this.database.equals(database) && this.collection.equals(collection);
    }
}
