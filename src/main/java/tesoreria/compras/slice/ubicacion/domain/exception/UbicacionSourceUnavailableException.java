package tesoreria.compras.slice.ubicacion.domain.exception;

public class UbicacionSourceUnavailableException extends RuntimeException {

    public UbicacionSourceUnavailableException(Throwable cause) {
        super("La fuente de ubicaciones no está disponible", cause);
    }
}
