package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

/**
 * Espeja {@code CompraCotizacionTramoResponse} de core
 * ({@code GET /api/tesoreria/core/compraCotizacionTramo}).
 */
public record CoreCotizacionTramoResponse(
        Long cotizacionTramoId,
        String nombre,
        Integer topeMultiplico,
        Integer cantidadMinima,
        Boolean abierto,
        Byte activo) {
}
