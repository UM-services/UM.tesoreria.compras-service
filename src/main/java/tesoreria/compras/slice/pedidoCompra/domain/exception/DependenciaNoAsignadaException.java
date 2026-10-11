package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class DependenciaNoAsignadaException extends RuntimeException {

    public DependenciaNoAsignadaException() {
        super("El usuario no tiene una dependencia asignada");
    }
}
