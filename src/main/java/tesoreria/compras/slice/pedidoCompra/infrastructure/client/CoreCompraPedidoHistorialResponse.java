package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreCompraPedidoHistorialResponse(
        Long compraPedidoHistorialId,
        Integer compraPedidoId,
        String estado,
        Integer usuarioId,
        String observacion,
        LocalDateTime fecha
) {
}
