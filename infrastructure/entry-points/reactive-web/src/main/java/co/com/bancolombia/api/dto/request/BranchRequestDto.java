package co.com.bancolombia.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public class BranchRequestDto {

    @JsonProperty("branchId")
    String branchId;

    @JsonProperty("franchiseId")
    String franchiseId;

    @JsonProperty("name")
    String name;

    @JsonProperty("address")
    String address;
}
