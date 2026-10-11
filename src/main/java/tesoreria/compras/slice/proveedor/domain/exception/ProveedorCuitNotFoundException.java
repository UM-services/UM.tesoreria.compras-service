package tesoreria.compras.slice.proveedor.domain.exception;

public class ProveedorCuitNotFoundException extends RuntimeException {

    public ProveedorCuitNotFoundException(String cuit, Throwable cause) {
        super("No existe el proveedor con CUIT " + cuit, cause);
    }
}
