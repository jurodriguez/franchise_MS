package co.com.bancolombia.dynamodb.branch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class BranchDynamo {

    @Getter(
            onMethod_ = {
                    @DynamoDbPartitionKey,
                    @DynamoDbAttribute("branchId")
            }
    )
    private String branchId;

    @Getter(
            onMethod_ = {
                    @DynamoDbSecondaryPartitionKey(indexNames = "ix-franchise-id"),
                    @DynamoDbAttribute("franchiseId")
            }
    )
    private String franchiseId;

    @Getter(
            onMethod_ = {
                    @DynamoDbAttribute("name")
            }
    )
    private String name;

    @Getter(
            onMethod_ = {
                    @DynamoDbAttribute("address")
            }
    )
    private String address;
}
