package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;

/** Ítem de pedido disponible para cotizar (pedido AUTORIZADO_PRESUPUESTO, aún sin proceso). */
public record CotizacionPendiente(
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
