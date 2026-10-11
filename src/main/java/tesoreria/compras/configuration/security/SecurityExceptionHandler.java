package tesoreria.compras.configuration.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del PEP ({@link RequierePermisoInterceptor}) al contrato
 * de error del servicio ({@code application/problem+json}).
 */
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(PermisoDenegadoException.class)
    ProblemDetail handlePermisoDenegado(PermisoDenegadoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(IdentidadRequeridaException.class)
    ProblemDetail handleIdentidad(IdentidadRequeridaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }
}
