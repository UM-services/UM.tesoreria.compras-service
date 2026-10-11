package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class IdentidadRequeridaException extends RuntimeException {

    public IdentidadRequeridaException() {
        super("Falta la identidad del usuario (header X-User-Id)");
    }
}
