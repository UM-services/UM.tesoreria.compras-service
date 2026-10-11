package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** El proceso de cotización enviado no es válido (400). */
public class CotizacionProcesoInvalidaException extends RuntimeException {

    public CotizacionProcesoInvalidaException(String message) {
        super(message);
    }
}
