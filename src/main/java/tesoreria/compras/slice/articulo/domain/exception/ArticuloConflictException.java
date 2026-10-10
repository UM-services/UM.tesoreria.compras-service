package tesoreria.compras.slice.articulo.domain.exception;

/**
 * La fuente (core) rechazó la operación por un conflicto (id duplicado al crear,
 * artículo referenciado al borrar, o escritura concurrente). Se responde 409.
 */
public class ArticuloConflictException extends RuntimeException {

    public ArticuloConflictException(Throwable cause) {
        super("La operación sobre el artículo choca con otro dato.", cause);
    }
}
