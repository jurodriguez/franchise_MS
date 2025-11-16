package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranchiseUseCaseTest {

    private FranchiseGateway franchiseGateway;
    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseGateway = Mockito.mock(FranchiseGateway.class);
        franchiseUseCase = new FranchiseUseCase(franchiseGateway);
    }

    @Test
    void createFranchiseSuccessfully() {
        Franchise franchise = new Franchise();
        franchise.setFranchiseId("F001");
        franchise.setName("Franchise Test");

        when(franchiseGateway.save(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseGateway, times(1)).save(franchise);
    }

    @Test
    void createFranchiseWithError() {
        Franchise franchise = new Franchise();
        franchise.setFranchiseId("F001");

        when(franchiseGateway.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Error al guardar")));

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException
                                && error.getMessage().equals("Error al guardar"))
                .verify();

        verify(franchiseGateway, times(1)).save(franchise);
    }
}
