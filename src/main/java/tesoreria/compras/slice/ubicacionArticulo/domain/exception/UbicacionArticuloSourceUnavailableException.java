package tesoreria.compras.slice.ubicacionArticulo.domain.exception;

public class UbicacionArticuloSourceUnavailableException extends RuntimeException {

    public UbicacionArticuloSourceUnavailableException(Throwable cause) {
        super("La fuente de imputaciones no está disponible", cause);
    }
}
