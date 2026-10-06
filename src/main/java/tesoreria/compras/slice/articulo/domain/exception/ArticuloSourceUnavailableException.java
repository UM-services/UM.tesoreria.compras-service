package tesoreria.compras.slice.articulo.domain.exception;

public class ArticuloSourceUnavailableException extends RuntimeException {

    public ArticuloSourceUnavailableException(Throwable cause) {
        super("La fuente de artículos no está disponible", cause);
    }
}
