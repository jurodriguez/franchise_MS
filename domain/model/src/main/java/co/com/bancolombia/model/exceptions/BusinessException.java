package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.enums.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class BusinessException extends FranchiseException {

    public BusinessException(ResponseMessage responseMessage) {
        super(responseMessage);
    }

    public BusinessException(String message, ResponseMessage responseMessage) {
        super(message, responseMessage);
    }

    public BusinessException(Throwable cause, ResponseMessage responseMessage) {
        super(cause, responseMessage);
    }
}
