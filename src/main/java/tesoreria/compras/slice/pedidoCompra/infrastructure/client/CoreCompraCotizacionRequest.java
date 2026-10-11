package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraCotizacionRequest(
        Integer ejercicioId,
        LocalDateTime fecha,
        String observacion,
        LocalDateTime fechaLimiteRespuesta,
        BigDecimal montoEstimado,
        Integer cantidadMinima,
        String excepcionMotivo,
        Integer usuarioId,
        List<CoreCompraCotizacionItemRequest> items,
        List<CoreCompraCotizacionProveedorRequest> proveedores) {
}
