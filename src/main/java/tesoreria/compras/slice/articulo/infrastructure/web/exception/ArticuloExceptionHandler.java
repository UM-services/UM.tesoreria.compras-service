package tesoreria.compras.slice.articulo.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;

@RestControllerAdvice
public class ArticuloExceptionHandler {

    @ExceptionHandler(ArticuloNotFoundException.class)
    ProblemDetail handleNotFound(ArticuloNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ArticuloSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(ArticuloSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
