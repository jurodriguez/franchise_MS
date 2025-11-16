package co.com.bancolombia.dynamodb.branch.repository;

import co.com.bancolombia.dynamodb.branch.model.BranchDynamo;
import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.branch.Branch;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class BranchRepository extends TemplateAdapterOperations<Branch, String, BranchDynamo> {

    public BranchRepository(
            @Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
            ObjectMapper mapper,
            @Value("${aws.dynamodb.tables.branch-data}") String tableName) {
        super(dynamoDbEnhancedAsyncClient, mapper, domain -> mapper.map(domain, Branch.class), tableName);
    }
}
