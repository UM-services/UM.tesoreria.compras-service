package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

public record CoreRecomendarCompraCotizacionRequest(
        Long compraCotizacionProveedorId,
        String fundamento) {
}
