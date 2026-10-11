package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

public record CotizacionTramoResponse(
        Long cotizacionTramoId,
        String nombre,
        Integer topeMultiplico,
        Integer cantidadMinima,
        Boolean abierto,
        Byte activo) {
}
