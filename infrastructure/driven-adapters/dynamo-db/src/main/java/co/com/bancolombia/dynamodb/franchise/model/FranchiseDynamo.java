package co.com.bancolombia.dynamodb.franchise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class FranchiseDynamo {

    @Getter(
            onMethod_ = {
                    @DynamoDbPartitionKey,
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
                    @DynamoDbAttribute("phone")
            }
    )
    private String phone;
}
