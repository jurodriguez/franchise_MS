package co.com.bancolombia.dynamodb.franchise.repository;

import co.com.bancolombia.dynamodb.franchise.model.FranchiseDynamo;
import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.franchise.Franchise;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class FranchiseRepository extends TemplateAdapterOperations<Franchise, String, FranchiseDynamo> {

    public FranchiseRepository(
            @Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
            ObjectMapper mapper,
            @Value("${aws.dynamodb.tables.franchise-data}") String tableName) {
        super(dynamoDbEnhancedAsyncClient, mapper, domain -> mapper.map(domain, Franchise.class), tableName);
    }
}
