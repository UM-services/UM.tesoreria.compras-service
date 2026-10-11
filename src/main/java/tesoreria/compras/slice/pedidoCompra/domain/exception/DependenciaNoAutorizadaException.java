package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class DependenciaNoAutorizadaException extends RuntimeException {

    public DependenciaNoAutorizadaException(Integer dependenciaId) {
        super("No está autorizado a decidir sobre pedidos de la dependencia " + dependenciaId);
    }
}
