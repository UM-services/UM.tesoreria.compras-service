package tesoreria.compras.slice.pedidoCompra.domain.exception;

/**
 * El mínimo de cotizaciones no puede resolverse: falta la referencia del ejercicio o no hay
 * tramos configurados. Se responde {@code 409} con {@code codigo: COTIZACIONES_NO_CONFIGURADAS}.
 */
public class CotizacionesNoConfiguradasException extends RuntimeException {

    public CotizacionesNoConfiguradasException() {
        super("No se puede resolver el mínimo de cotizaciones (sin referencia del ejercicio o sin tramos configurados)");
    }
}
