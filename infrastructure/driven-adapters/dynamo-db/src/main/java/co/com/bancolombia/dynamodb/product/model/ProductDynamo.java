package co.com.bancolombia.dynamodb.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class ProductDynamo {

    @Getter(
            onMethod_ = {
                    @DynamoDbPartitionKey,
                    @DynamoDbAttribute("productId")
            }
    )
    private String productId;

    @Getter(
            onMethod_ = {
                    @DynamoDbSortKey,
                    @DynamoDbSecondaryPartitionKey(indexNames = "ix-branch-id"),
                    @DynamoDbAttribute("branchId")
            }
    )
    private String branchId;

    @Getter(
            onMethod_ = {
                    @DynamoDbAttribute("name")
            }
    )
    private String name;

    @Getter(
            onMethod_ = {
                    @DynamoDbAttribute("stock")
            }
    )
    private Integer stock;
}
