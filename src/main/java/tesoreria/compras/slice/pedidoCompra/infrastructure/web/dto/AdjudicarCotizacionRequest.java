package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/**
 * Adjudicación de una oferta del proceso. {@code compraCotizacionProveedorId} es la oferta elegida;
 * si es {@code null} se adjudica la recomendada por compras.
 */
public record AdjudicarCotizacionRequest(
        Long compraCotizacionProveedorId,
        String fundamento) {
}
