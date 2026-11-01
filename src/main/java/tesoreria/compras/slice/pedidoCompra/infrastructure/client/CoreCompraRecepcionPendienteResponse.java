package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CoreCompraRecepcionPendienteResponse(
        Long compraFormalizacionId,
        String numero,
        String numeroExterno,
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fecha,
        BigDecimal monto,
        String moneda,
        List<CoreCompraRecepcionPendienteItemResponse> items,
        List<CoreCompraRecepcionResponse> recepciones,
        Boolean completo) {
}
