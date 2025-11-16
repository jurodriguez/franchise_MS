package co.com.bancolombia.dynamodb.branch.adapter;

import co.com.bancolombia.dynamodb.branch.repository.BranchRepository;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchGateway;
import co.com.bancolombia.model.enums.ResponseMessage;
import co.com.bancolombia.model.exceptions.TechnicalException;
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
public class BranchAdapter implements BranchGateway {

    private final BranchRepository branchRepository;

    @Override
    public Mono<Branch> save(Branch branch) {
        return branchRepository.save(branch)
                .doOnSuccess(keySaved -> log.info("Success saving in dynamodb", kv(
                        "saveDynamoDBResponse",
                        keySaved)))
                .doOnError(error -> log.error("Error saving in dynamodb", kv("saveDynamoDBError",
                        error)))
                .onErrorMap(exception -> new TechnicalException(exception, ResponseMessage.INTERNAL_ERROR))
                .doOnSubscribe(subscription ->
                        log.info("Request saving in dynamodb", kv("saveDynamodbRequest ", branch)));
    }

    @Override
    public Flux<Branch> getBranchesByFranchise(String franchiseId) {

        QueryConditional query = QueryConditional
                .keyEqualTo(k -> k.partitionValue(franchiseId));

        return getBranchesByQuery(query)
                .doOnSuccess(list -> log.info("Success get keys expired in DynamoDB - Found {}", list.size()))
                .doOnError(error -> log.error("Error get keys expired in DynamoDB", error))
                .onErrorMap(exception -> new TechnicalException(exception, ResponseMessage.INTERNAL_ERROR))
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<List<Branch>> getBranchesByQuery(QueryConditional queryConditional) {

        return branchRepository.queryByIndex(QueryEnhancedRequest.builder()
                        .queryConditional(queryConditional).build(),
                "ix-franchise-id");

    }
}
