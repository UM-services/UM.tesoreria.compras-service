package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/**
 * Mínimo de cotizaciones requerido por un pedido (snapshot o cálculo vigente). {@code cantidadMinima}
 * puede ser {@code null} si el pedido aún no tiene monto estimado.
 */
public record CotizacionesMinimasResponse(Integer compraPedidoId, Integer cantidadMinima) {
}
