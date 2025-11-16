package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.BranchRequestDto;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.branch.BranchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BranchHandlerTest {

    private BranchUseCase branchUseCase;
    private BranchHandler branchHandler;

    @BeforeEach
    void setUp() {
        branchUseCase = Mockito.mock(BranchUseCase.class);
        branchHandler = new BranchHandler(branchUseCase);
    }

    @Test
    void saveBranchSuccessfully() {
        BranchRequestDto request = BranchRequestDto.builder()
                .branchId("B01")
                .name("Sucursal test")
                .address("Calle 123")
                .franchiseId("F01")
                .build();

        Branch returned = new Branch();
        returned.setBranchId("B01");

        when(branchUseCase.create(any())).thenReturn(Mono.just(returned));

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request));

        Mono<ServerResponse> response = branchHandler.saveBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().value() == 201)
                .verifyComplete();

        verify(branchUseCase, times(1)).create(any());
    }

    @Test
    void saveBranchWithEmptyBody() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.empty());

        Mono<ServerResponse> response = branchHandler.saveBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();
    }

    @Test
    void saveBranchThrowsException() {
        BranchRequestDto request = BranchRequestDto.builder()
                .branchId("B02")
                .name("Sucursal Error")
                .address("Calle Error")
                .franchiseId("F02")
                .build();

        when(branchUseCase.create(any()))
                .thenReturn(Mono.error(new RuntimeException("Error en use case")));

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request));

        Mono<ServerResponse> response = branchHandler.saveBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();

        verify(branchUseCase, times(1)).create(any());
    }
}
