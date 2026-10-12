package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;

/** Filtros de consulta de Órdenes de Compra (todos opcionales). */
public record FormalizacionFiltro(
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
