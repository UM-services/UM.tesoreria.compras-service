package tesoreria.compras.slice.proveedor.domain.exception;

/**
 * La fuente (core) rechazó la operación por conflicto (por ejemplo, una escritura
 * concurrente). Se responde 409.
 */
public class ProveedorConflictException extends RuntimeException {

    public ProveedorConflictException(Throwable cause) {
        super("La operación sobre el proveedor choca con otro dato.", cause);
    }
}
