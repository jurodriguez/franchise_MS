package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductGateway {
    Mono<Product> save(Product product);

    Mono<Void> deleteProductFromBranch(String branchId, String productId);

    Flux<Product> getProductsByBranch(String franchiseId);

    Mono<Product> getById(String productId);

    Mono<Product> update(Product product);
}
