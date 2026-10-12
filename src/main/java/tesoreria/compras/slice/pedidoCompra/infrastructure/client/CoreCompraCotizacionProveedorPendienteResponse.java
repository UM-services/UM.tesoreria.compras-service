package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraCotizacionProveedorPendienteResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fechaLimiteRespuesta,
        CoreCompraCotizacionProveedorResponse proveedor,
        List<CoreCompraCotizacionItemResponse> items) {
}
