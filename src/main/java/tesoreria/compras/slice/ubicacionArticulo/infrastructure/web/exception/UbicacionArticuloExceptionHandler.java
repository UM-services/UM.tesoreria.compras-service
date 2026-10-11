package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloConflictException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloSourceUnavailableException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloValidationException;

@RestControllerAdvice
public class UbicacionArticuloExceptionHandler {

    @ExceptionHandler(UbicacionArticuloValidationException.class)
    ProblemDetail handleValidation(UbicacionArticuloValidationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UbicacionArticuloConflictException.class)
    ProblemDetail handleConflict(UbicacionArticuloConflictException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UbicacionArticuloSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(UbicacionArticuloSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
