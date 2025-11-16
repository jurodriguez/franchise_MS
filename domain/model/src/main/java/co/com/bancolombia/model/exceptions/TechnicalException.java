package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.enums.ResponseMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends FranchiseException {

    public TechnicalException(ResponseMessage responseMessage) {
        super(responseMessage);
    }

    public TechnicalException(String message, ResponseMessage responseMessage) {
        super(message, responseMessage);
    }

    public TechnicalException(Throwable cause, ResponseMessage responseMessage) {
        super(cause, responseMessage);
    }
}
