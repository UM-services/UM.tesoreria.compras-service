package tesoreria.compras.slice.proveedor.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;

@RestControllerAdvice
public class ProveedorExceptionHandler {

    @ExceptionHandler(ProveedorNotFoundException.class)
    ProblemDetail handleNotFound(ProveedorNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ProveedorSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(ProveedorSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
