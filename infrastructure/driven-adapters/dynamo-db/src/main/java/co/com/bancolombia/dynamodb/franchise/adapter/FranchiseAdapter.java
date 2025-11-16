package co.com.bancolombia.dynamodb.franchise.adapter;

import co.com.bancolombia.dynamodb.franchise.repository.FranchiseRepository;
import co.com.bancolombia.model.enums.ResponseMessage;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@AllArgsConstructor
public class FranchiseAdapter implements FranchiseGateway {

    private final FranchiseRepository franchiseRepository;
    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return franchiseRepository.save(franchise)
                .doOnSuccess(keySaved -> log.info("Success saving in dynamodb", kv(
                        "saveDynamoDBResponse",
                        keySaved)))
                .doOnError(error -> log.error("Error saving in dynamodb", kv("saveDynamoDBError",
                        error)))
                .onErrorMap(exception -> new TechnicalException(exception, ResponseMessage.INTERNAL_ERROR))
                .doOnSubscribe(subscription ->
                        log.info("Request saving in dynamodb", kv("saveDynamodbRequest ", franchise)));
    }

}
