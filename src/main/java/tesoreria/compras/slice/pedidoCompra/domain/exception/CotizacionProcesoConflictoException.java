package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** El proceso de cotización entra en conflicto de estado (409). */
public class CotizacionProcesoConflictoException extends RuntimeException {

    public CotizacionProcesoConflictoException(String message) {
        super(message);
    }
}
