package tesoreria.compras.proveedor.domain.exception;

public class ProveedorNotFoundException extends RuntimeException {

    private final Integer proveedorId;

    public ProveedorNotFoundException(Integer proveedorId, Throwable cause) {
        super("No existe el proveedor con ID " + proveedorId, cause);
        this.proveedorId = proveedorId;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }
}
