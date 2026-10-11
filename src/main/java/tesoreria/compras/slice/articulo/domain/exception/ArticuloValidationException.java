package tesoreria.compras.slice.articulo.domain.exception;

/**
 * La fuente (core) rechazó el cuerpo o los parámetros de la operación. Se responde 400.
 */
public class ArticuloValidationException extends RuntimeException {

    public ArticuloValidationException(Throwable cause) {
        super("El artículo enviado no es válido.", cause);
    }
}
