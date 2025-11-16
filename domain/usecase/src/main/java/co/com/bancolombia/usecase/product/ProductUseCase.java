package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.gateways.BranchGateway;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductGateway productGateway;

    private final BranchGateway branchGateway;

    public Mono<Product> create(Product product) {
        return productGateway.save(product);
    }

    public Mono<Void> deleteProductFromBranch(String branchId, String productId) {
        return productGateway.deleteProductFromBranch(branchId, productId);
    }

    public Mono<Product> updateStock(String productId, Integer stock) {
        return productGateway.updateStock(productId, stock);
    }

    public Flux<Product> findTopProductsByBranch(String franchiseId) {

        return branchGateway.getBranchesByFranchise(franchiseId)
                .flatMap(branch ->
                        productGateway.getProductsByBranch(branch.getBranchId())
                                .sort((p1, p2) -> p2.getStock().compareTo(p1.getStock()))
                                .next()
                                .map(product -> {
                                    product.setBranchId(branch.getBranchId());
                                    return product;
                                })
                );
    }
}
