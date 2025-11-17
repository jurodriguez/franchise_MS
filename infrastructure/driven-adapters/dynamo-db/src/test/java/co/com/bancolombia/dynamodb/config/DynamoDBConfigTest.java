package co.com.bancolombia.dynamodb.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DynamoDBConfigTest {

    String dynamoEndpoint = "http://localhost:4566/";
    DynamoDBConfig dynamoDbConfig;

    @BeforeEach
    void setUp() {
        System.setProperty("aws.region", "us-west-1");
        dynamoDbConfig = new DynamoDBConfig();
    }

    @Test
    void getDynamoDbEnhancedAsyncClient() {
        dynamoDbConfig.setEndpoint(dynamoEndpoint);
        assertNotNull(dynamoDbConfig.getDynamoDbEnhancedAsyncClient());
    }

}