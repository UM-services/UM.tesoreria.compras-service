package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreEstimarCompraPedidoRequest(BigDecimal montoEstimado, String fuenteEstimacion, Integer usuarioId) {
}
