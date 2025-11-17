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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(productRepository.save(any())).thenReturn(Mono.just(product));

        StepVerifier.create(productAdapter.save(product))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).save(product);
    }

    @Test
    void saveProductThrowsException() {
        Product product = new Product();

        when(productRepository.save(any())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.save(product))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void deleteProductSuccessfully() {
        Product product = new Product();

        when(productRepository.getByKeyValueAndSortKey("P01", "B01"))
                .thenReturn(Mono.just(product));
        when(productRepository.delete(any())).thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.deleteProductFromBranch("B01", "P01"))
                .verifyComplete();

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProductNotFound() {
        when(productRepository.getByKeyValueAndSortKey("P01", "B01"))
                .thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.deleteProductFromBranch("B01", "P01"))
                .expectErrorMatches(e ->
                        e instanceof TechnicalException &&
                                ((TechnicalException) e).getResponseMessage() == ResponseMessage.NOT_FOUND
                )
                .verify();
    }

    @Test
    void deleteProductThrowsUnexpectedException() {
        when(productRepository.getByKeyValueAndSortKey(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.deleteProductFromBranch("B01", "P01"))
                .expectErrorMatches(e ->
                        e instanceof TechnicalException &&
                                ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR)
                .verify();
    }

    @Test
    void updateProductSuccessfully() {
        Product product = new Product();

        when(productRepository.update(any())).thenReturn(Mono.just(product));

        StepVerifier.create(productAdapter.update(product))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).update(product);
    }

    @Test
    void updateProductThrowsTechnicalException() {

        when(productRepository.update(any()))
                .thenReturn(Mono.error(new TechnicalException("Error", ResponseMessage.INTERNAL_ERROR)));

        StepVerifier.create(productAdapter.update(new Product()))
                .expectError(TechnicalException.class)
                .verify();
    }

    @Test
    void updateProductThrowsUnexpectedException() {

        when(productRepository.update(any()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected")));

        StepVerifier.create(productAdapter.update(new Product()))
                .expectErrorMatches(e ->
                        e instanceof TechnicalException &&
                                ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR)
                .verify();
    }

    @Test
    void getByIdSuccessfully() {
        Product product = new Product();

        when(productRepository.getById("P01"))
                .thenReturn(Mono.just(product));

        StepVerifier.create(productAdapter.getById("P01"))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).getById("P01");
    }

    @Test
    void getByIdNotFound() {

        when(productRepository.getById("P01"))
                .thenReturn(Mono.empty());

        StepVerifier.create(productAdapter.getById("P01"))
                .expectErrorMatches(e ->
                        e instanceof TechnicalException &&
                                ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR &&
                                e.getCause() instanceof TechnicalException &&
                                ((TechnicalException) e.getCause()).getResponseMessage() == ResponseMessage.NOT_FOUND
                )
                .verify();
    }

    @Test
    void getByIdUnexpectedError() {

        when(productRepository.getById("P01"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.getById("P01"))
                .expectErrorMatches(e ->
                        e instanceof TechnicalException &&
                                ((TechnicalException) e).getResponseMessage() == ResponseMessage.INTERNAL_ERROR)
                .verify();
    }

    @Test
    void getProductsByBranchSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");

        when(productRepository.queryByIndex(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(product)));

        StepVerifier.create(productAdapter.getProductsByBranch("B01"))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void getProductsByBranchThrowsException() {

        when(productRepository.queryByIndex(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productAdapter.getProductsByBranch("B01"))
                .expectError(TechnicalException.class)
                .verify();
    }
}
