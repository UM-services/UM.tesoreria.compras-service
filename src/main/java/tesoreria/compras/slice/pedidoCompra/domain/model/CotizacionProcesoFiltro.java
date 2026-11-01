package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.time.LocalDateTime;

public record CotizacionProcesoFiltro(
        String estado,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
