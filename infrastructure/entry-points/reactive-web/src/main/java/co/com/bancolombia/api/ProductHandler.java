package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.ProductRequestDto;
import co.com.bancolombia.api.dto.request.UpdateStockRequestDto;
import co.com.bancolombia.api.mapper.ProductMapper;
import co.com.bancolombia.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> saveProduct(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ProductRequestDto.class)
                .switchIfEmpty(ServerResponse.badRequest()
                        .bodyValue("Cuerpo vacio")
                        .flatMap(resp -> Mono.error(new RuntimeException("Cuerpo vacio"))))
                .map(ProductMapper.MAPPER::toProduct)
                .flatMap(productUseCase::create)
                .flatMap(product -> ServerResponse.status(HttpStatus.CREATED).bodyValue(product))
                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");

        return productUseCase.deleteProductFromBranch(branchId, productId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String productId = request.pathVariable("productId");

        return request.bodyToMono(UpdateStockRequestDto.class)
                .flatMap(dto -> productUseCase.updateStock(productId, dto.getStock()))
                .flatMap(updated -> ServerResponse.ok().bodyValue(updated));
    }

    public Mono<ServerResponse> getTopProductsByStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");

        return productUseCase.findTopProductsByBranch(franchiseId)
                .collectList()
                .flatMap(list -> ServerResponse.ok().bodyValue(list));
    }
}
