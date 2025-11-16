package co.com.bancolombia.dynamodb.franchise;

import co.com.bancolombia.dynamodb.franchise.adapter.FranchiseAdapter;
import co.com.bancolombia.dynamodb.franchise.repository.FranchiseRepository;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.franchise.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranchiseAdapterTest {

    private FranchiseRepository franchiseRepository;
    private FranchiseAdapter franchiseAdapter;

    @BeforeEach
    void setUp() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        franchiseAdapter = new FranchiseAdapter(franchiseRepository);
    }

    @Test
    void saveFranchiseSuccessfully() {
        Franchise franchise = new Franchise();
        franchise.setFranchiseId("F001");
        franchise.setName("Test Franchise");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseAdapter.save(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).save(franchise);
    }

    @Test
    void saveFranchiseThrowsTechnicalException() {
        Franchise franchise = new Franchise();
        franchise.setFranchiseId("F002");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("DynamoDB error")));

        StepVerifier.create(franchiseAdapter.save(franchise))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof TechnicalException);
                    assertTrue(error.getCause() instanceof RuntimeException);
                    assertEquals("DynamoDB error", error.getCause().getMessage());
                })
                .verify();

        verify(franchiseRepository, times(1)).save(franchise);
    }

}
