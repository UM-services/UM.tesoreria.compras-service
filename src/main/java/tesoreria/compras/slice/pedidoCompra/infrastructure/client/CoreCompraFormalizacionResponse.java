package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraFormalizacionResponse(
        Long compraFormalizacionId,
        Long compraAdjudicacionId,
        String numero,
        String numeroExterno,
        Integer ejercicioId,
        LocalDateTime fecha,
        Integer proveedorId,
        String moneda,
        BigDecimal monto,
        String condicionPago,
        String plazoEntrega,
        String garantia,
        String objeto,
        String observacion,
        Integer usuarioId,
        List<CoreCompraFormalizacionItemResponse> items,
        LocalDateTime created,
        LocalDateTime updated) {
}
