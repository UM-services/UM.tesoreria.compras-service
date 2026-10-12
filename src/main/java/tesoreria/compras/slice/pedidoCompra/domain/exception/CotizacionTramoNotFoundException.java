package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** Tramo de cotizaciones inexistente. */
public class CotizacionTramoNotFoundException extends RuntimeException {

    public CotizacionTramoNotFoundException(Long cotizacionTramoId) {
        super("No existe el tramo de cotizaciones con id: " + cotizacionTramoId);
    }
}
