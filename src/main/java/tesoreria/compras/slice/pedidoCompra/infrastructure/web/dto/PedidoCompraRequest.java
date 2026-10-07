package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Alta/edición de un pedido. La identidad (solicitante/dependencia/facultad/sede) la resuelve
 * la fachada desde el usuario logueado; {@code enviar} dispara el envío en la misma llamada.
 */
public record PedidoCompraRequest(
        String necesidad,
        LocalDateTime fechaRequerida,
        Boolean urgente,
        String urgenciaMotivo,
        Boolean montoConocido,
        BigDecimal montoEstimado,
        String fuenteEstimacion,
        List<PedidoCompraItemRequest> items,
        boolean enviar
) {
}
