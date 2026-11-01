package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

public record CotizacionTramoRequest(
        String nombre,
        Integer topeMultiplico,
        Integer cantidadMinima,
        Byte activo) {
}
