package tesoreria.compras.slice.sheet.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.sheet.domain.exception.SheetSourceUnavailableException;

@RestControllerAdvice
public class SheetExceptionHandler {

    @ExceptionHandler(SheetSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(SheetSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
