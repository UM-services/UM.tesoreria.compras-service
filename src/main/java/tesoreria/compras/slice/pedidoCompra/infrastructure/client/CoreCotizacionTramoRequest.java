package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

/**
 * Cuerpo para alta/edición de un tramo de cotizaciones en core.
 * Espeja {@code CompraCotizacionTramoRequest} de core.
 */
public record CoreCotizacionTramoRequest(
        String nombre,
        Integer topeMultiplico,
        Integer cantidadMinima,
        Byte activo) {
}
