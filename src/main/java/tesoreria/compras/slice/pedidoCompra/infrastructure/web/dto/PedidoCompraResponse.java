package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoCompraResponse(
        Integer compraPedidoId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fecha,
        String estado,
        Integer autorizanteId,
        Integer solicitanteId,
        Integer dependenciaId,
        Integer facultadId,
        Integer geograficaId,
        String necesidad,
        LocalDateTime fechaRequerida,
        Boolean urgente,
        String urgenciaMotivo,
        Boolean montoConocido,
        BigDecimal montoEstimado,
        String fuenteEstimacion,
        LocalDateTime fechaEnvio,
        String rechazoMotivo,
        String descartadoMotivo,
        String dependenciaNombre,
        String solicitanteNombre,
        List<PedidoCompraItemResponse> items
) {
}
