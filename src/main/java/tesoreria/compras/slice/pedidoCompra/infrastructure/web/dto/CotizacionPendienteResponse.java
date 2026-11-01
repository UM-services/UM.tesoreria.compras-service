package tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record CotizacionPendienteResponse(
        Integer compraPedidoItemId,
        Integer compraPedidoId,
        String pedidoNumero,
        Integer dependenciaId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        String referenciaWeb) {
}
