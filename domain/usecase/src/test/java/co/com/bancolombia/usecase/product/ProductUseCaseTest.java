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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        StepVerifier.create(productUseCase.create(product))
                .expectNext(product)
                .verifyComplete();

        verify(productGateway).save(product);
    }

    @Test
    void createProductError() {
        Product product = new Product();
        product.setProductId("P02");

        when(productGateway.save(any()))
                .thenReturn(Mono.error(new RuntimeException("Error guardando")));

        StepVerifier.create(productUseCase.create(product))
                .expectErrorMatches(e -> e.getMessage().equals("Error guardando"))
                .verify();

        verify(productGateway).save(product);
    }

    @Test
    void deleteProductSuccessfully() {
        when(productGateway.deleteProductFromBranch("B01", "P01"))
                .thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductFromBranch("B01", "P01"))
                .verifyComplete();

        verify(productGateway).deleteProductFromBranch("B01", "P01");
    }

    @Test
    void updateStockSuccessfully() {
        Product product = new Product();
        product.setProductId("P01");
        product.setStock(100);

        when(productGateway.getById("P01"))
                .thenReturn(Mono.just(product));

        when(productGateway.update(any(Product.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productUseCase.updateStock("P01", 150))
                .expectNextMatches(p -> p.getStock() == 150)
                .verifyComplete();

        verify(productGateway).getById("P01");
        verify(productGateway).update(any(Product.class));
    }

    @Test
    void updateStockProductNotFound() {
        when(productGateway.getById("P01"))
                .thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock("P01", 150))
                .verifyComplete();

        verify(productGateway).getById("P01");
        verify(productGateway, never()).update(any());
    }


    @Test
    void updateStockWithErrorOnGetById() {
        when(productGateway.getById("P01"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productUseCase.updateStock("P01", 150))
                .expectErrorMatches(e -> e.getMessage().equals("DB error"))
                .verify();

        verify(productGateway).getById("P01");
        verify(productGateway, never()).update(any());
    }

    @Test
    void findTopProductsByBranchSuccessfully() {

        Branch branch = new Branch();
        branch.setBranchId("B01");
        branch.setFranchiseId("F01");

        Product p1 = new Product();
        p1.setProductId("P01");
        p1.setStock(50);

        Product p2 = new Product();
        p2.setProductId("P02");
        p2.setStock(120);

        when(branchGateway.getBranchesByFranchise("F01"))
                .thenReturn(Flux.just(branch));

        when(productGateway.getProductsByBranch("B01"))
                .thenReturn(Flux.just(p1, p2));

        StepVerifier.create(productUseCase.findTopProductsByBranch("F01"))
                .expectNextMatches(p -> p.getProductId().equals("P02") &&
                        p.getBranchId().equals("B01"))
                .verifyComplete();

        verify(branchGateway).getBranchesByFranchise("F01");
        verify(productGateway).getProductsByBranch("B01");
    }

    @Test
    void findTopProductsBranchWithoutProducts() {

        Branch branch = new Branch();
        branch.setBranchId("B01");

        when(branchGateway.getBranchesByFranchise("F01"))
                .thenReturn(Flux.just(branch));

        when(productGateway.getProductsByBranch("B01"))
                .thenReturn(Flux.empty());

        StepVerifier.create(productUseCase.findTopProductsByBranch("F01"))
                .verifyComplete();
    }

    @Test
    void findTopProductsNoBranches() {
        when(branchGateway.getBranchesByFranchise("F01"))
                .thenReturn(Flux.empty());

        StepVerifier.create(productUseCase.findTopProductsByBranch("F01"))
                .verifyComplete();
    }
}
