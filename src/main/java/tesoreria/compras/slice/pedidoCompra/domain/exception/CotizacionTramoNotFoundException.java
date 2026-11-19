package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** Tramo de presupuestos inexistente. */
public class CotizacionTramoNotFoundException extends RuntimeException {

    public CotizacionTramoNotFoundException(Long cotizacionTramoId) {
        super("No existe el tramo de presupuestos con id: " + cotizacionTramoId);
    }
}
