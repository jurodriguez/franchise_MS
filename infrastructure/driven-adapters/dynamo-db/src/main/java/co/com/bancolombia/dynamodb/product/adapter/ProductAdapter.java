package co.com.bancolombia.dynamodb.product.adapter;

import co.com.bancolombia.dynamodb.product.repository.ProductRepository;
import co.com.bancolombia.model.enums.ResponseMessage;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@AllArgsConstructor
public class ProductAdapter implements ProductGateway {

    private final ProductRepository productRepository;

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(product)
                .doOnSuccess(keySaved -> log.info("Success saving in dynamodb", kv(
                        "saveDynamoDBResponse",
                        keySaved)))
                .doOnError(error -> log.error("Error saving in dynamodb", kv("saveDynamoDBError",
                        error)))
                .onErrorMap(exception -> new TechnicalException(exception, ResponseMessage.INTERNAL_ERROR))
                .doOnSubscribe(subscription ->
                        log.info("Request saving in dynamodb", kv("saveDynamodbRequest ", product)));
    }

    @Override
    public Mono<Void> deleteProductFromBranch(String branchId, String productId) {
        return productRepository.getByKeyValueAndSortKey(productId, branchId)
                .switchIfEmpty(Mono.error(new TechnicalException("producto no encontrado", ResponseMessage.NOT_FOUND)))
                .flatMap(product -> {
                    log.info("Producto eliminado", kv("productId", productId), kv("branchId", branchId));
                    return productRepository.delete(product);
                })
                .doOnSubscribe(sub -> log.info("Eliminar producto",
                        kv("branchId", branchId), kv("productId", productId)))
                .doOnError(error -> log.error("Error eliminando el producto",
                        kv("deleteError", error)))
                .onErrorMap(e -> {
                    if (e instanceof TechnicalException) {
                        return e;
                    }
                    return new TechnicalException(e, ResponseMessage.INTERNAL_ERROR);
                }).then();
    }

    @Override
    public Mono<Product> updateStock(String productId, Integer stock) {
        return productRepository.getById(productId)
                .switchIfEmpty(Mono.error(new TechnicalException("producto no encontrado", ResponseMessage.NOT_FOUND)))
                .flatMap(product -> {
                    product.setStock(stock);
                    return productRepository.update(product);
                })
                .onErrorMap(error -> {
                    if (error instanceof TechnicalException) {
                        return error;
                    }
                    return new TechnicalException(error, ResponseMessage.INTERNAL_ERROR);
                })
                .doOnSubscribe(sub -> log.info("Actualizar stock", kv("productId", productId), kv("stock", stock)));
    }


    @Override
    public Flux<Product> getProductsByBranch(String branchId) {
        QueryConditional query = QueryConditional
                .keyEqualTo(k -> k.partitionValue(branchId));

        return getBranchesByQuery(query)
                .doOnSuccess(list -> log.info("Success get keys expired in DynamoDB - Found {}", list.size()))
                .doOnError(error -> log.error("Error get keys expired in DynamoDB", error))
                .onErrorMap(exception -> new TechnicalException(exception, ResponseMessage.INTERNAL_ERROR))
                .flatMapMany(Flux::fromIterable);
    }


    private Mono<List<Product>> getBranchesByQuery(
            QueryConditional queryConditional) {

        return productRepository.queryByIndex(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional).build(),
                "ix-branch-id");

    }

}
