package tesoreria.compras.slice.pedidoCompra.domain.exception;

/** El proceso de cotización no existe (404). */
public class CotizacionProcesoNotFoundException extends RuntimeException {

    public CotizacionProcesoNotFoundException(Long compraCotizacionId) {
        super("No existe el proceso de cotización con id: " + compraCotizacionId);
    }
}
