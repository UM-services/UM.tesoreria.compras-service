package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

import java.util.List;

public interface CompraPedidoGateway {

    PedidoCompra crear(PedidoCompra pedido);

    PedidoCompra actualizar(Integer compraPedidoId, PedidoCompra pedido);

    PedidoCompra enviar(Integer compraPedidoId);

    PedidoCompra getById(Integer compraPedidoId);

    List<PedidoCompra> listarPorSolicitante(Long solicitanteId);
}
