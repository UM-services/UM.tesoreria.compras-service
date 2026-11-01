package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.time.LocalDateTime;

public record CoreCerrarCompraRecepcionRequest(
        String motivo,
        Integer usuarioId) {
}
