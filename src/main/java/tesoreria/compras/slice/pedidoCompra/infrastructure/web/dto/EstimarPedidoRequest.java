package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

/**
 * Cuerpo de la revisión de compras: valor estimado (y fuente opcional) del pedido.
 */
public record EstimarPedidoRequest(BigDecimal montoEstimado, String fuenteEstimacion) {
}
