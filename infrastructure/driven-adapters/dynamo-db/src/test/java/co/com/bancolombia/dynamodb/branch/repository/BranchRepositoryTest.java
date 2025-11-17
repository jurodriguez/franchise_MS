package co.com.bancolombia.dynamodb.branch.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class BranchRepositoryTest {

    @Test
    void initTransactionDataRepository(){
        DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient = mock(
                DynamoDbEnhancedAsyncClient.class);
        ObjectMapper mapper = mock(ObjectMapper.class);
        BranchRepository branchRepository =
                new BranchRepository(dynamoDbEnhancedAsyncClient, mapper, "tableName");
        Assertions.assertNotNull(branchRepository);
    }
}