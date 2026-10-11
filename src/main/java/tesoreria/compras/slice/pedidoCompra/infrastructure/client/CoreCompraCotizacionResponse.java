package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Espeja {@code CompraCotizacionResponse} de core. */
public record CoreCompraCotizacionResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fecha,
        String estado,
        String observacion,
        LocalDateTime fechaLimiteRespuesta,
        BigDecimal montoEstimado,
        Integer cantidadMinima,
        String excepcionMotivo,
        LocalDateTime fechaEnvio,
        Long proveedorRecomendadoId,
        String fundamentoRecomendacion,
        LocalDateTime fechaRecomendacion,
        Integer usuarioId,
        List<CoreCompraCotizacionItemResponse> items,
        List<CoreCompraCotizacionProveedorResponse> proveedores) {
}
