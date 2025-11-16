package co.com.bancolombia.model.branch;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {

    private String branchId;

    private String franchiseId;

    private String name;

    private String address;
}
