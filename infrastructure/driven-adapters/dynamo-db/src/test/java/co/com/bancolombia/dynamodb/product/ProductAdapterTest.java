package co.com.bancolombia.dynamodb.product;

import co.com.bancolombia.dynamodb.product.adapter.ProductAdapter;
import co.com.bancolombia.dynamodb.product.repository.ProductRepository;
import co.com.bancolombia.model.enums.ResponseMessage;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductAdapterTest {

    private ProductRepository productRepository;
    private ProductAdapter productAdapter;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productAdapter = new ProductAdapter(productRepository);
    }

    @Test
    void saveProductSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        product.setBranchId("B01");

        when(productRepository.save(any())).thenReturn(Mono.just(product));

        StepVerifier.create(productAdapter.save(product))
                .expectNextMatches(p -> p.getProductId().equals("P01"))
                .verifyComplete();

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void saveProductThrowsException() {
        Product product = new Product();
        when(productRepository.save(any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.save(product))
                .expectErrorMatches(e -> e instanceof TechnicalException &&
                        ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR)
                .verify();
    }

    @Test
    void deleteProductSuccessfully() {
        Product product = new Product();
        when(productRepository.getByKeyValueAndSortKey("P01", "B01")).thenReturn(Mono.just(product));
        when(productRepository.delete(product)).thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.deleteProductFromBranch("B01", "P01"))
                .verifyComplete();

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProductNotFound() {
        when(productRepository.getByKeyValueAndSortKey("P01", "B01")).thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.deleteProductFromBranch("B01", "P01"))
                .expectErrorMatches(e -> e instanceof TechnicalException &&
                        ((TechnicalException) e).getResponseMessage() == ResponseMessage.NOT_FOUND)
                .verify();
    }

    @Test
    void updateStockSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        product.setStock(10);

        when(productRepository.getById("P01")).thenReturn(Mono.just(product));
        when(productRepository.update(any())).thenReturn(Mono.just(product));

        StepVerifier.create(productAdapter.updateStock("P01", 50))
                .expectNextMatches(p -> p.getStock() == 50)
                .verifyComplete();

        verify(productRepository, times(1)).update(product);
    }

    @Test
    void updateStockNotFound() {
        when(productRepository.getById("P01")).thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.updateStock("P01", 50))
                .expectErrorMatches(e -> e instanceof TechnicalException &&
                        ((TechnicalException) e).getResponseMessage() == ResponseMessage.NOT_FOUND &&
                        "producto no encontrado".equals(e.getMessage()))
                .verify();
    }

    @Test
    void getProductsByBranchSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        when(productRepository.queryByIndex(any(), any())).thenReturn(Mono.just(Collections.singletonList(product)));

        StepVerifier.create(productAdapter.getProductsByBranch("B01"))
                .expectNextMatches(p -> p.getProductId().equals("P01"))
                .verifyComplete();

        verify(productRepository, times(1)).queryByIndex(any(), any());
    }

    @Test
    void getProductsByBranchThrowsException() {
        when(productRepository.queryByIndex(any(), any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.getProductsByBranch("B01"))
                .expectErrorMatches(e -> e instanceof TechnicalException &&
                        ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR)
                .verify();
    }
}
