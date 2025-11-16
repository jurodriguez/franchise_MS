package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchGateway {
    Mono<Branch> save(Branch branch);

    Flux<Branch> getBranchesByFranchise(String franchiseId);
}
