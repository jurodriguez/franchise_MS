package co.com.bancolombia.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public class ProductRequestDto {

    @JsonProperty("productId")
    String productId;

    @JsonProperty("branchId")
    String branchId;

    @JsonProperty("name")
    String name;

    @JsonProperty("stock")
    Integer stock;
}
