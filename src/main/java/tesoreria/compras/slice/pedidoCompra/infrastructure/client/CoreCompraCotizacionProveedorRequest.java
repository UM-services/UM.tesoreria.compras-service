package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

public record CoreCompraCotizacionProveedorRequest(
        Integer proveedorId,
        String email,
        String observacion) {
}
