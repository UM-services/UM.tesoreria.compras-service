package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

/**
 * Espeja {@code LimiteAutorizacionResponse} de core
 * ({@code GET /api/tesoreria/core/compraAutoridadUsuario/limite/{usuarioId}}).
 */
public record CoreLimiteAutorizacionResponse(
        Integer usuarioId,
        Integer ejercicioId,
        Integer multiplico,
        BigDecimal referencia,
        BigDecimal limite,
        Boolean ilimitado,
        Boolean tieneAutoridad) {
}
