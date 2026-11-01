package tesoreria.compras.slice.pedidoCompra.domain.model;

/**
 * Tramo de cotizaciones mínimas por monto. Espeja el contrato de core; la fachada no persiste.
 *
 * <p>El {@code topeMultiplico} se expresa como múltiplo de la referencia del ejercicio;
 * {@code abierto} indica el tramo sin tope (el último).</p>
 */
public record CotizacionTramo(
        Long cotizacionTramoId,
        String nombre,
        Integer topeMultiplico,
        Integer cantidadMinima,
        Boolean abierto,
        Byte activo) {
}
