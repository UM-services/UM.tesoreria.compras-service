package tesoreria.compras.slice.ubicacionArticulo.domain.exception;

/**
 * La fuente (core) rechazó el cuerpo o los parámetros de la asignación. 400.
 */
public class UbicacionArticuloValidationException extends RuntimeException {

    public UbicacionArticuloValidationException(Throwable cause) {
        super("La asignación de imputación no es válida.", cause);
    }
}
