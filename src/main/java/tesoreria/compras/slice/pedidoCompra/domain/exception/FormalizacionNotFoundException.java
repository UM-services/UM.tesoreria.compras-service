package tesoreria.compras.slice.pedidoCompra.domain.exception;

public class FormalizacionNotFoundException extends RuntimeException {

    public FormalizacionNotFoundException(Long compraFormalizacionId) {
        super("No existe la Orden de Compra con id: " + compraFormalizacionId);
    }

    public FormalizacionNotFoundException(Long compraCotizacionId, boolean porCotizacion) {
        super("El proceso de cotización " + compraCotizacionId + " no tiene Orden de Compra");
    }

    public FormalizacionNotFoundException(String message) {
        super(message);
    }
}
