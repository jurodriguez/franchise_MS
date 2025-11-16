package co.com.bancolombia.model.product;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {

    private String productId;

    private String branchId;

    private String name;

    private Integer stock;
}
