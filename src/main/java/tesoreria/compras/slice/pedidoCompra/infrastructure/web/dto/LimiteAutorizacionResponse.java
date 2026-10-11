package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

/**
 * Límite de autorización por monto del usuario para un ejercicio.
 */
public record LimiteAutorizacionResponse(
        Long usuarioId,
        Integer ejercicioId,
        Integer multiplico,
        BigDecimal referencia,
        BigDecimal limite,
        Boolean ilimitado,
        Boolean tieneAutoridad) {
}
