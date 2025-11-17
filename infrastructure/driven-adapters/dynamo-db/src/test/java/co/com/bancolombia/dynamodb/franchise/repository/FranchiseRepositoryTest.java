package co.com.bancolombia.dynamodb.franchise.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryTest {

    @Test
    void initTransactionDataRepository(){
        DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient = mock(
                DynamoDbEnhancedAsyncClient.class);
        ObjectMapper mapper = mock(ObjectMapper.class);
        FranchiseRepository franchiseRepository =
                new FranchiseRepository(dynamoDbEnhancedAsyncClient, mapper, "tableName");
        Assertions.assertNotNull(franchiseRepository);
    }
}