package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Proceso de cotización (espeja el contrato de core; la fachada no persiste).
 */
public record CotizacionProceso(
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
        List<CotizacionProcesoItem> items,
        List<CotizacionProcesoProveedor> proveedores) {
}
