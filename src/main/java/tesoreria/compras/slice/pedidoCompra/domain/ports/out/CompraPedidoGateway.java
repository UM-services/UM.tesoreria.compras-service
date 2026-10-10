package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;

import java.math.BigDecimal;
import java.util.List;

public interface CompraPedidoGateway {

    PedidoCompra crear(PedidoCompra pedido);

    PedidoCompra actualizar(Integer compraPedidoId, PedidoCompra pedido);

    PedidoCompra enviar(Integer compraPedidoId, Long usuarioId);

    PedidoCompra aprobar(Integer compraPedidoId, Long autorizanteId);

    PedidoCompra rechazar(Integer compraPedidoId, Long autorizanteId, String motivo);

    PedidoCompra descartar(Integer compraPedidoId, Long usuarioId, String motivo);

    PedidoCompra estimar(Integer compraPedidoId, Long usuarioId, BigDecimal monto, String fuente);

    PedidoCompra autorizarPresupuesto(Integer compraPedidoId, Long usuarioId);

    PedidoCompra rechazarPresupuesto(Integer compraPedidoId, Long usuarioId, String motivo);

    PedidoCompra getById(Integer compraPedidoId);

    List<PedidoCompra> listarPorSolicitante(Long solicitanteId);

    List<PedidoCompra> listar(PedidoCompraFiltro filtro);
}
