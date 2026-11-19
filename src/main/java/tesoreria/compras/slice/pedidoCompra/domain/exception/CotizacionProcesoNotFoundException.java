package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** El proceso de presupuesto no existe (404). */
public class CotizacionProcesoNotFoundException extends RuntimeException {

    public CotizacionProcesoNotFoundException(Long compraCotizacionId) {
        super("No existe el proceso de presupuesto con id: " + compraCotizacionId);
    }
}
