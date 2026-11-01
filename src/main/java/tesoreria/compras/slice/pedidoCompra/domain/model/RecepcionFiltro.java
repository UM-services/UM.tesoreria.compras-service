package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;

/** Filtros de consulta de recepciones (todos opcionales). */
public record RecepcionFiltro(
        Integer ejercicioId,
        Long compraFormalizacionId,
        String resultado,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
