package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;

public record PedidoCompraHistorialResponse(
        Long compraPedidoHistorialId,
        Integer compraPedidoId,
        String estado,
        Integer usuarioId,
        String observacion,
        LocalDateTime fecha
) {
}
