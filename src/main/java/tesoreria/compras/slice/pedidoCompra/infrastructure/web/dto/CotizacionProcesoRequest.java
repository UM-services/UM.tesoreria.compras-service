package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CotizacionProcesoRequest(
        Integer ejercicioId,
        LocalDateTime fecha,
        String observacion,
        LocalDateTime fechaLimiteRespuesta,
        BigDecimal montoEstimado,
        Integer cantidadMinima,
        String excepcionMotivo,
        Integer usuarioId,
        List<CotizacionProcesoItemRequest> items,
        List<CotizacionProcesoProveedorRequest> proveedores) {
}
