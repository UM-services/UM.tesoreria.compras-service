package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion;

/**
 * Límite de autorización por monto del usuario para un ejercicio (resuelto en `core`).
 */
public interface AutoridadGateway {

    LimiteAutorizacion getLimite(Long usuarioId, Integer ejercicioId);
}
