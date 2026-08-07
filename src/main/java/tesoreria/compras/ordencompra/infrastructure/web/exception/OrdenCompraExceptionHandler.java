package tesoreria.compras.ordencompra.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNoEditableException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNotFoundException;
import tesoreria.compras.ordencompra.domain.exception.TransicionInvalidaException;

/**
 * Acotado al paquete de órdenes y a sus excepciones de dominio. No captura
 * IllegalArgumentException ni IllegalStateException genéricas: un bug del servidor debe
 * seguir siendo un 500 y no filtrar su mensaje al cliente.
 */
@RestControllerAdvice(basePackages = "tesoreria.compras.ordencompra")
public class OrdenCompraExceptionHandler {

    @ExceptionHandler(OrdenCompraNotFoundException.class)
    ProblemDetail notFound(OrdenCompraNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(OrdenCompraInvalidaException.class)
    ProblemDetail badRequest(OrdenCompraInvalidaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(OrdenCompraNoEditableException.class)
    ProblemDetail noEditable(OrdenCompraNoEditableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    ProblemDetail transicionInvalida(TransicionInvalidaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
