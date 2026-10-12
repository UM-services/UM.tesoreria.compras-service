package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** Conflicto al configurar tramos (p. ej. tope duplicado). */
public class CotizacionTramoConflictoException extends RuntimeException {

    public CotizacionTramoConflictoException(String message) {
        super(message);
    }
}
