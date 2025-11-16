package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.FranchiseRequestDto;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
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

class FranchiseHandlerTest {

    private FranchiseUseCase franchiseUseCase;
    private FranchiseHandler franchiseHandler;

    @BeforeEach
    void setUp() {
        franchiseUseCase = Mockito.mock(FranchiseUseCase.class);
        franchiseHandler = new FranchiseHandler(franchiseUseCase);
    }

    @Test
    void saveFranchiseSuccessfully() {
        FranchiseRequestDto request = FranchiseRequestDto.builder()
                .franchiseId("F01")
                .name("Franquicia test")
                .phone("999999")
                .build();

        Franchise returned = new Franchise();
        returned.setFranchiseId("F01");

        when(franchiseUseCase.create(any())).thenReturn(Mono.just(returned));

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request));

        Mono<ServerResponse> response = franchiseHandler.saveFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().value() == 201)
                .verifyComplete();

        verify(franchiseUseCase, times(1)).create(any());
    }


    @Test
    void saveFranchiseWithEmptyBody() {

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.saveFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();
    }


    @Test
    void saveFranchiseThrowsException() {
        FranchiseRequestDto request = FranchiseRequestDto.builder()
                .franchiseId("F02")
                .name("Test")
                .phone("111")
                .build();

        when(franchiseUseCase.create(any()))
                .thenReturn(Mono.error(new RuntimeException("Error en use case")));

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request));

        Mono<ServerResponse> response = franchiseHandler.saveFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(resp -> resp.statusCode().is5xxServerError())
                .verifyComplete();

        verify(franchiseUseCase, times(1)).create(any());
    }

}
