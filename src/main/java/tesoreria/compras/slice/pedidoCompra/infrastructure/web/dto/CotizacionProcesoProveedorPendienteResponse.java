package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Documento de proveedor pendiente de respuesta (bandeja "Cotizaciones pedidas"). */
public record CotizacionProcesoProveedorPendienteResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fechaLimiteRespuesta,
        CotizacionProcesoProveedorResponse proveedor,
        List<CotizacionProcesoItemResponse> items) {
}
