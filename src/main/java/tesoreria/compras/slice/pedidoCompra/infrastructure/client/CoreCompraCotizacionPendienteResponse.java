package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import java.math.BigDecimal;

public record CoreCompraCotizacionPendienteResponse(
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
