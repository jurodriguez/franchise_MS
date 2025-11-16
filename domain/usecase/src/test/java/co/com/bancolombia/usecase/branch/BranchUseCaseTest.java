package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BranchUseCaseTest {

    @Mock
    private BranchGateway branchGateway;

    private BranchUseCase branchUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        branchUseCase = new BranchUseCase(branchGateway);
    }

    @Test
    void createBranchSuccessfully() {
        Branch branch = new Branch();
        branch.setBranchId("B001");
        branch.setName("Sucursal 1");

        when(branchGateway.save(any())).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.create(branch))
                .expectNextMatches(result ->
                        result.getBranchId().equals("B001") &&
                                result.getName().equals("Sucursal 1"))
                .verifyComplete();

        verify(branchGateway, times(1)).save(branch);
    }

    @Test
    void createBranchThrowsException() {
        Branch branch = new Branch();
        branch.setBranchId("B002");

        when(branchGateway.save(any()))
                .thenReturn(Mono.error(new RuntimeException("DynamoDB error")));

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("DynamoDB error")
                )
                .verify();

        verify(branchGateway, times(1)).save(branch);
    }
}
