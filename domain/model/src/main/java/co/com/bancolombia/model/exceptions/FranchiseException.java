package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.enums.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FranchiseException extends RuntimeException {

    private final ResponseMessage responseMessage;

    public FranchiseException(String message, ResponseMessage responseMessage) {
        super(message);
        this.responseMessage = responseMessage;
    }

    public FranchiseException(Throwable cause, ResponseMessage responseMessage) {
        super(cause);
        this.responseMessage = responseMessage;
    }
}
