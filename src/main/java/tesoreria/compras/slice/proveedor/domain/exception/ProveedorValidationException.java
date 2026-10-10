package tesoreria.compras.slice.proveedor.domain.exception;

/**
 * La fuente (core) rechazó el cuerpo o los parámetros de la operación. Se responde 400.
 */
public class ProveedorValidationException extends RuntimeException {

    public ProveedorValidationException(Throwable cause) {
        super("El proveedor enviado no es válido.", cause);
    }
}
