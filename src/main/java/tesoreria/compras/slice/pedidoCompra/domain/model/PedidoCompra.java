package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido de compra (cabecera + detalle). Espeja el contrato de core; la fachada no persiste.
 */
public record PedidoCompra(
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
        List<PedidoCompraItem> items
) {

    /**
     * Devuelve una copia con la identidad resuelta por la fachada (solicitante y su dependencia),
     * que el cliente no envía.
     */
    public PedidoCompra conIdentidad(Long solicitanteId, Integer dependenciaId, Integer facultadId,
                                     Integer geograficaId) {
        return new PedidoCompra(
                compraPedidoId, numero, ejercicioId, fecha, estado, autorizanteId,
                solicitanteId == null ? null : solicitanteId.intValue(),
                dependenciaId, facultadId, geograficaId, necesidad, fechaRequerida, urgente,
                urgenciaMotivo, montoConocido, montoEstimado, fuenteEstimacion, items);
    }
}
