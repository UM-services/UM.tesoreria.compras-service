package tesoreria.compras.slice.pedidoCompra.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tesoreria.compras.slice.pedidoCompra.domain.exception.*;

@RestControllerAdvice
public class PedidoCompraExceptionHandler {

    @ExceptionHandler(PedidoCompraNotFoundException.class)
    ProblemDetail handleNotFound(PedidoCompraNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PedidoCompraEstadoInvalidoException.class)
    ProblemDetail handleConflict(PedidoCompraEstadoInvalidoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(PermisoDenegadoException.class)
    ProblemDetail handlePermiso(PermisoDenegadoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(DependenciaNoAutorizadaException.class)
    ProblemDetail handleDependenciaNoAutorizada(DependenciaNoAutorizadaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(LimiteAutorizacionExcedidoException.class)
    ProblemDetail handleLimiteExcedido(LimiteAutorizacionExcedidoException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        problem.setProperty("codigo", "LIMITE_AUTORIZACION_EXCEDIDO");
        problem.setProperty("monto", exception.getMonto());
        problem.setProperty("limite", exception.getLimite());
        return problem;
    }

    @ExceptionHandler(AccesoPedidoDenegadoException.class)
    ProblemDetail handleAccesoPedidoDenegado(AccesoPedidoDenegadoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(IdentidadRequeridaException.class)
    ProblemDetail handleIdentidad(IdentidadRequeridaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(DependenciaNoAsignadaException.class)
    ProblemDetail handleDependencia(DependenciaNoAsignadaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(PedidoCompraSourceUnavailableException.class)
    ProblemDetail handleSourceUnavailable(PedidoCompraSourceUnavailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(CotizacionTramoInvalidaException.class)
    ProblemDetail handleCotizacionInvalida(CotizacionTramoInvalidaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CotizacionTramoConflictoException.class)
    ProblemDetail handleCotizacionConflicto(CotizacionTramoConflictoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CotizacionTramoNotFoundException.class)
    ProblemDetail handleCotizacionNotFound(CotizacionTramoNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CotizacionesNoConfiguradasException.class)
    ProblemDetail handleCotizacionesNoConfiguradas(CotizacionesNoConfiguradasException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setProperty("codigo", "COTIZACIONES_NO_CONFIGURADAS");
        return problem;
    }

    @ExceptionHandler(CotizacionProcesoInvalidaException.class)
    ProblemDetail handleCotizacionProcesoInvalida(CotizacionProcesoInvalidaException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CotizacionProcesoConflictoException.class)
    ProblemDetail handleCotizacionProcesoConflicto(CotizacionProcesoConflictoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CotizacionProcesoNotFoundException.class)
    ProblemDetail handleCotizacionProcesoNotFound(CotizacionProcesoNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
