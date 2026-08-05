package tesoreria.compras.proveedor.domain.exception;

public class ProveedorSourceUnavailableException extends RuntimeException {

    public ProveedorSourceUnavailableException(Throwable cause) {
        super("La fuente de proveedores no está disponible", cause);
    }
}
