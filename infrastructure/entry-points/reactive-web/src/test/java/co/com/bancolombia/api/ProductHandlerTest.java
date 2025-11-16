package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.ProductRequestDto;
import co.com.bancolombia.api.dto.request.UpdateStockRequestDto;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductHandlerTest {

    private ProductUseCase productUseCase;
    private ProductHandler productHandler;

    @BeforeEach
    void setUp() {
        productUseCase = Mockito.mock(ProductUseCase.class);
        productHandler = new ProductHandler(productUseCase);
    }

    @Test
    void saveProductSuccessfully() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productId("P01")
                .branchId("B01")
                .name("Producto test")
                .stock(100)
                .build();

        Product savedProduct = new Product();
        savedProduct.setProductId("P01");
        savedProduct.setBranchId("B01");
        savedProduct.setName("Producto test");
        savedProduct.setStock(100);

        when(productUseCase.create(any())).thenReturn(Mono.just(savedProduct));

        ServerRequest request = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestDto));

        Mono<ServerResponse> response = productHandler.saveProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().value() == 201)
                .verifyComplete();

        verify(productUseCase, times(1)).create(any());
    }

    @Test
    void saveProductWithEmptyBody() {
        ServerRequest request = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.empty());

        Mono<ServerResponse> response = productHandler.saveProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();
    }

    @Test
    void saveProductThrowsException() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productId("P02")
                .branchId("B01")
                .name("Error Producto")
                .stock(50)
                .build();

        when(productUseCase.create(any()))
                .thenReturn(Mono.error(new RuntimeException("Error en use case")));

        ServerRequest request = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(requestDto));

        Mono<ServerResponse> response = productHandler.saveProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();

        verify(productUseCase, times(1)).create(any());
    }

    @Test
    void deleteProductSuccessfully() {
        when(productUseCase.deleteProductFromBranch("B01", "P01")).thenReturn(Mono.empty());

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("branchId", "B01")
                .pathVariable("productId", "P01")
                .build();

        Mono<ServerResponse> response = productHandler.deleteProduct(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(productUseCase, times(1)).deleteProductFromBranch("B01", "P01");
    }

    @Test
    void updateStockSuccessfully() {
        UpdateStockRequestDto dto = UpdateStockRequestDto.builder()
                .branchId("B01")
                .stock(50)
                .build();

        Product updatedProduct = new Product();
        updatedProduct.setProductId("P01");
        updatedProduct.setBranchId("B01");
        updatedProduct.setStock(50);

        when(productUseCase.updateStock("P01", 50)).thenReturn(Mono.just(updatedProduct));

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("productId", "P01")
                .body(Mono.just(dto));

        Mono<ServerResponse> response = productHandler.updateStock(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(productUseCase, times(1)).updateStock("P01", 50);
    }

    @Test
    void getTopProductsByStockSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        product.setBranchId("B01");
        product.setStock(100);

        when(productUseCase.findTopProductsByBranch("F01")).thenReturn(Flux.just(product));

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F01")
                .build();

        Mono<ServerResponse> response = productHandler.getTopProductsByStock(request);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(productUseCase, times(1)).findTopProductsByBranch("F01");
    }
}
