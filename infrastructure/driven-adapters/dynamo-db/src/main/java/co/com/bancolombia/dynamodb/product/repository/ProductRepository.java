package co.com.bancolombia.dynamodb.product.repository;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.dynamodb.product.model.ProductDynamo;
import co.com.bancolombia.model.product.Product;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class ProductRepository extends TemplateAdapterOperations<Product, String, ProductDynamo> {

    public ProductRepository(
            @Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
            ObjectMapper mapper,
            @Value("${aws.dynamodb.tables.product-data}") String tableName) {
        super(dynamoDbEnhancedAsyncClient, mapper, domain -> mapper.map(domain, Product.class), tableName);
    }
}
