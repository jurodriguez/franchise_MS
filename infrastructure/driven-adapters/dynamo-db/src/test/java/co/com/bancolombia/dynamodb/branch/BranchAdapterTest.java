package co.com.bancolombia.dynamodb.branch.adapter;

import co.com.bancolombia.dynamodb.branch.repository.BranchRepository;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.exceptions.TechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BranchAdapterTest {

    @Mock
    private BranchRepository branchRepository;

    private BranchAdapter branchAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        branchAdapter = new BranchAdapter(branchRepository);
    }

    @Test
    void saveBranchSuccessfully() {
        Branch branch = new Branch();
        branch.setBranchId("B001");

        when(branchRepository.save(any()))
                .thenReturn(Mono.just(branch));

        StepVerifier.create(branchAdapter.save(branch))
                .expectNext(branch)
                .verifyComplete();

        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    void saveBranchThrowsTechnicalException() {
        Branch branch = new Branch();
        branch.setBranchId("B002");

        when(branchRepository.save(any()))
                .thenReturn(Mono.error(new RuntimeException("Dynamo error")));

        StepVerifier.create(branchAdapter.save(branch))
                .expectErrorSatisfies(error -> {
                    assert (error instanceof TechnicalException);
                    assert (error.getCause() instanceof RuntimeException);
                    assert (error.getCause().getMessage().equals("Dynamo error"));
                })
                .verify();

        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    void getBranchesByFranchiseSuccessfully() {
        Branch b1 = new Branch();
        b1.setBranchId("B01");
        b1.setFranchiseId("F1");

        Branch b2 = new Branch();
        b2.setBranchId("B02");
        b2.setFranchiseId("F1");

        when(branchRepository.queryByIndex(any(), anyString()))
                .thenReturn(Mono.just(List.of(b1, b2)));

        StepVerifier.create(branchAdapter.getBranchesByFranchise("F1"))
                .expectNext(b1)
                .expectNext(b2)
                .verifyComplete();

        verify(branchRepository, times(1))
                .queryByIndex(any(), eq("ix-franchise-id"));
    }

    @Test
    void getBranchesByFranchiseThrowsTechnicalException() {

        when(branchRepository.queryByIndex(any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("DynamoDB failure")));

        StepVerifier.create(branchAdapter.getBranchesByFranchise("F1"))
                .expectErrorSatisfies(error -> {
                    assert (error instanceof TechnicalException);
                    assert (error.getCause() instanceof RuntimeException);
                    assert (error.getCause().getMessage().equals("DynamoDB failure"));
                })
                .verify();

        verify(branchRepository, times(1))
                .queryByIndex(any(), eq("ix-franchise-id"));
    }
}
