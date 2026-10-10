package tesoreria.compras.slice.proveedor.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorConflictException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorCuitNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorNotFoundException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorSourceUnavailableException;
import tesoreria.compras.slice.proveedor.domain.exception.ProveedorValidationException;

@RestControllerAdvice
public class ProveedorExceptionHandler {

    @ExceptionHandler(ProveedorNotFoundException.class)
    ProblemDetail handleNotFound(ProveedorNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ProveedorCuitNotFoundException.class)
    ProblemDetail handleCuitNotFound(ProveedorCuitNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ProveedorValidationException.class)
    ProblemDetail handleValidation(ProveedorValidationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ProveedorConflictException.class)
    ProblemDetail handleConflict(ProveedorConflictException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ProveedorSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(ProveedorSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
