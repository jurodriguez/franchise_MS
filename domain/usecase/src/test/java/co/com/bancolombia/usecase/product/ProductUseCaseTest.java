package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchGateway;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductUseCaseTest {

    private ProductGateway productGateway;
    private BranchGateway branchGateway;
    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        productGateway = Mockito.mock(ProductGateway.class);
        branchGateway = Mockito.mock(BranchGateway.class);
        productUseCase = new ProductUseCase(productGateway, branchGateway);
    }

    @Test
    void createProductSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");

        when(productGateway.save(any())).thenReturn(Mono.just(product));

        Mono<Product> result = productUseCase.create(product);

        StepVerifier.create(result)
                .expectNextMatches(p -> p.getProductId().equals("P01"))
                .verifyComplete();

        verify(productGateway, times(1)).save(product);
    }

    @Test
    void createProductThrowsException() {
        Product product = new Product();
        product.setProductId("P02");

        when(productGateway.save(any())).thenReturn(Mono.error(new RuntimeException("Error guardando")));

        StepVerifier.create(productUseCase.create(product))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Error guardando"))
                .verify();

        verify(productGateway, times(1)).save(product);
    }

    @Test
    void deleteProductSuccessfully() {
        when(productGateway.deleteProductFromBranch("B01", "P01")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductFromBranch("B01", "P01"))
                .verifyComplete();

        verify(productGateway, times(1)).deleteProductFromBranch("B01", "P01");
    }

    @Test
    void updateStockSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        product.setStock(100);

        when(productGateway.updateStock("P01", 150)).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.updateStock("P01", 150))
                .expectNextMatches(p -> p.getProductId().equals("P01"))
                .verifyComplete();

        verify(productGateway, times(1)).updateStock("P01", 150);
    }

    @Test
    void findTopProductsByBranchSuccessfully() {
        Branch branch1 = new Branch();
        branch1.setBranchId("B01");
        branch1.setFranchiseId("F01");

        Product product1 = new Product();
        product1.setProductId("P01");
        product1.setStock(50);

        Product product2 = new Product();
        product2.setProductId("P02");
        product2.setStock(100);

        when(branchGateway.getBranchesByFranchise("F01")).thenReturn(Flux.just(branch1));
        when(productGateway.getProductsByBranch("B01")).thenReturn(Flux.just(product1, product2));

        StepVerifier.create(productUseCase.findTopProductsByBranch("F01"))
                .expectNextMatches(p -> p.getProductId().equals("P02") && p.getBranchId().equals("B01"))
                .verifyComplete();

        verify(branchGateway, times(1)).getBranchesByFranchise("F01");
        verify(productGateway, times(1)).getProductsByBranch("B01");
    }
}
