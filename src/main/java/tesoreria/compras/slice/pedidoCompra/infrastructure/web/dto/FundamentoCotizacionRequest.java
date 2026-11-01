package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

/** Motivo de una decisión que no adjudica (devolver a compras / no adjudicar). */
public record FundamentoCotizacionRequest(
        String fundamento) {
}
