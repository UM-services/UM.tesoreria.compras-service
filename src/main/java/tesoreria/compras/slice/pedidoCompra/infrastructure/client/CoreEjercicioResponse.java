package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

/**
 * Espeja el ejercicio actual de core ({@code GET /ejercicio/last}); sólo se usa su id.
 */
public record CoreEjercicioResponse(Integer ejercicioId) {
}
