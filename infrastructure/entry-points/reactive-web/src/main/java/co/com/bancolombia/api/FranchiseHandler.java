package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.FranchiseRequestDto;
import co.com.bancolombia.api.mapper.FranchiseMapper;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
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
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> saveFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FranchiseRequestDto.class)
                .switchIfEmpty(ServerResponse.badRequest().bodyValue("Cuerpo vacio")
                        .flatMap(serverResponse -> Mono.error(new RuntimeException("Cuerpo vacio"))))
                .map(FranchiseMapper.MAPPER::toFranchise)
                .flatMap(franchiseUseCase::create)
                .flatMap(franchise -> ServerResponse.status(HttpStatus.CREATED).bodyValue(franchise))
                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
