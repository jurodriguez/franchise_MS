package co.com.bancolombia.dynamodb.helper;

import co.com.bancolombia.dynamodb.product.model.ProductDynamo;
import co.com.bancolombia.dynamodb.product.repository.ProductRepository;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    private ProductRepository repository;
    private DynamoDbAsyncTable<ProductDynamo> table;
    private DynamoDbAsyncIndex<ProductDynamo> index;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

        mapper = Mockito.mock(ObjectMapper.class);

        DynamoDbEnhancedAsyncClient client = Mockito.mock(DynamoDbEnhancedAsyncClient.class);

        table = Mockito.mock(DynamoDbAsyncTable.class);

        index = Mockito.mock(DynamoDbAsyncIndex.class);

        when(client.table(anyString(), any(TableSchema.class))).thenReturn(table);
        when(table.index(anyString())).thenReturn(index);

        repository = new ProductRepository(
                client,
                mapper,
                "dummy-product-table"
        );
    }

    @Test
    void saveSuccessfully() {
        Product product = new Product();
        ProductDynamo dynamo = new ProductDynamo();

        when(mapper.map(product, ProductDynamo.class)).thenReturn(dynamo);
        when(table.putItem(any(ProductDynamo.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(dynamo));

        StepVerifier.create(repository.save(product))
                .expectNext(product)
                .verifyComplete();

        verify(table, atLeastOnce()).putItem(any(ProductDynamo.class));
    }

    @Test
    void updateSuccessfully() {
        Product product = new Product();
        ProductDynamo dynamo = new ProductDynamo();

        when(mapper.map(product, ProductDynamo.class)).thenReturn(dynamo);
        when(table.updateItem(any(ProductDynamo.class)))
                .thenReturn(CompletableFuture.completedFuture(dynamo));

        StepVerifier.create(repository.update(product))
                .expectNext(product)
                .verifyComplete();

        verify(table).updateItem(any(ProductDynamo.class));
    }

    @Test
    void deleteSuccessfully() {

        Product product = new Product();
        ProductDynamo dynamo = new ProductDynamo();

        when(mapper.map(product, ProductDynamo.class)).thenReturn(dynamo);
        when(mapper.map(dynamo, Product.class)).thenReturn(product);

        when(table.deleteItem(any(ProductDynamo.class)))
                .thenReturn(CompletableFuture.completedFuture(dynamo));

        StepVerifier.create(repository.delete(product))
                .expectNext(product)
                .verifyComplete();

        verify(table).deleteItem(any(ProductDynamo.class));
    }
}
