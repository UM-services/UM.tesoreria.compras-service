package tesoreria.compras.slice.ubicacionArticulo.domain.exception;

/**
 * La fuente (core) rechazó la asignación por conflicto (escritura concurrente). 409.
 */
public class UbicacionArticuloConflictException extends RuntimeException {

    public UbicacionArticuloConflictException(Throwable cause) {
        super("La asignación choca con otro dato.", cause);
    }
}
