package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

import java.math.BigDecimal;

/**
 * Revisión del dpto. de compras: carga/confirma el valor estimado del pedido enviado.
 */
public interface EstimarPedidoCompraUseCase {

    PedidoCompra estimar(Integer compraPedidoId, Long usuarioId, BigDecimal monto, String fuente);
}
