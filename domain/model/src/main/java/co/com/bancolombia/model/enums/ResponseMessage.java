package co.com.bancolombia.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseMessage {

    NOT_FOUND("404", "Recurso no encontrado"),
    INTERNAL_ERROR("500", "Error Interno"),
    DYNAMO_DB_NOT_FOUND_RECORD(INTERNAL_ERROR.getCode(), "Error Interno al buscar una franquisia");

    private final String code;
    private final String message;

}