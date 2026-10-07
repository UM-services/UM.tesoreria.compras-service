package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CoreCompraPedidoResponse(
        Integer compraPedidoId,
        String numero,
        Integer ejercicioId,
        java.time.LocalDateTime fecha,
        String estado,
        Integer autorizanteId,
        Integer solicitanteId,
        Integer dependenciaId,
        Integer facultadId,
        Integer geograficaId,
        String necesidad,
        java.time.LocalDateTime fechaRequerida,
        Boolean urgente,
        String urgenciaMotivo,
        Boolean montoConocido,
        java.math.BigDecimal montoEstimado,
        String fuenteEstimacion,
        java.util.List<CoreCompraPedidoItemResponse> items
) {
}
