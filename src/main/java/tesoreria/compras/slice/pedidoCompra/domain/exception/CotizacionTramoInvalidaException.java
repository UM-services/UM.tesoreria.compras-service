package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** Configuración de tramos inválida (rechazada por core). */
public class CotizacionTramoInvalidaException extends RuntimeException {

    public CotizacionTramoInvalidaException() {
        super("La configuración del tramo de cotizaciones es inválida");
    }
}
