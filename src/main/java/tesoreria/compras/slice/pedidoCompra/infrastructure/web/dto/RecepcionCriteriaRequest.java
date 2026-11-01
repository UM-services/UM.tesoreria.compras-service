package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;

/** Filtros de consulta de recepciones. */
public record RecepcionCriteriaRequest(
        Integer ejercicioId,
        Long compraFormalizacionId,
        String resultado,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
