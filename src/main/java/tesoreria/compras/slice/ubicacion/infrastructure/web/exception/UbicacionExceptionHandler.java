package tesoreria.compras.slice.ubicacion.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.ubicacion.domain.exception.UbicacionSourceUnavailableException;

@RestControllerAdvice
public class UbicacionExceptionHandler {

    @ExceptionHandler(UbicacionSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(UbicacionSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
