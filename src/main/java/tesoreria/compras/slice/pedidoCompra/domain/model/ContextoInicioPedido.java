package tesoreria.compras.slice.pedidoCompra.domain.model;

/**
 * Contexto que la fachada entrega al formulario "iniciar pedido" en una sola llamada.
 */
public record ContextoInicioPedido(Solicitante solicitante, DependenciaInfo dependencia) {
}
