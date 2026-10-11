package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/**
 * Límite de autorización por monto de un usuario para un ejercicio, resuelto por `core`.
 *
 * <ul>
 *   <li>{@code tieneAutoridad}: el usuario tiene al menos un perfil de autoridad activo.</li>
 *   <li>{@code ilimitado}: alguno de sus perfiles es "sin límite".</li>
 *   <li>{@code limite}: {@code multiplico × referencia}; {@code null} si es ilimitado o si no
 *       puede calcularse (sin referencia cargada para el ejercicio).</li>
 * </ul>
 */
public record LimiteAutorizacion(
        Long usuarioId,
        Integer ejercicioId,
        Integer multiplico,
        BigDecimal referencia,
        BigDecimal limite,
        boolean ilimitado,
        boolean tieneAutoridad) {
}
