package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

public record RecomendarCotizacionRequest(
        Long compraCotizacionProveedorId,
        String fundamento) {
}
