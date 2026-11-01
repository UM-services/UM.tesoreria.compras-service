package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.util.List;

/**
 * Respuesta de la bandeja del autorizante. Además de los pedidos devuelve las dependencias
 * habilitadas del usuario y la marca {@code sinDependencias}, para que el cliente pueda
 * distinguir una bandeja vacía por falta de asignación de una vacía por filtro.
 */
public record BandejaPedidoResponse(
        List<PedidoCompraResponse> pedidos,
        List<Integer> dependenciaIds,
        boolean sinDependencias
) {
}
