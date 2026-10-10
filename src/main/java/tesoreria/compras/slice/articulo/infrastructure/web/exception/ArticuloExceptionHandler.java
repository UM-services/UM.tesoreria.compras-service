package tesoreria.compras.slice.articulo.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloConflictException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloValidationException;

@RestControllerAdvice
public class ArticuloExceptionHandler {

    @ExceptionHandler(ArticuloNotFoundException.class)
    ProblemDetail handleNotFound(ArticuloNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ArticuloValidationException.class)
    ProblemDetail handleValidation(ArticuloValidationException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ArticuloConflictException.class)
    ProblemDetail handleConflict(ArticuloConflictException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ArticuloSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(ArticuloSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
