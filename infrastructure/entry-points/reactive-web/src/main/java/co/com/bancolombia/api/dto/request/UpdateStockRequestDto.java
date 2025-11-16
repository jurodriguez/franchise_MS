package co.com.bancolombia.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public class UpdateStockRequestDto {

    @JsonProperty("branchId")
    String branchId;

    @JsonProperty("stock")
    Integer stock;
}
