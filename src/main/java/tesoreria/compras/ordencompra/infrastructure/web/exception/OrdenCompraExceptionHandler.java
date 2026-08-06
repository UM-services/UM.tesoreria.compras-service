package tesoreria.compras.ordencompra.infrastructure.web.exception;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class OrdenCompraExceptionHandler { @ExceptionHandler(IllegalArgumentException.class) ProblemDetail badRequest(IllegalArgumentException e){return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());} @ExceptionHandler(IllegalStateException.class) ProblemDetail conflict(IllegalStateException e){return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,e.getMessage());} }
