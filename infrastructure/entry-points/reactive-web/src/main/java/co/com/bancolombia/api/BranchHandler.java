package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.BranchRequestDto;
import co.com.bancolombia.api.mapper.BranchMapper;
import co.com.bancolombia.usecase.branch.BranchUseCase;
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
public class BranchHandler {

    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> saveBranch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequestDto.class)
                .switchIfEmpty(Mono.error(new RuntimeException("Cuerpo vacio")))
                .map(BranchMapper.MAPPER::toBranch)
                .flatMap(branchUseCase::create)
                .flatMap(branchMono -> ServerResponse.status(HttpStatus.CREATED).bodyValue(branchMono))
                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
