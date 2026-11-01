package tesoreria.compras.slice.pedidoCompra.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden de Compra (etapa 8) resuelta en {@code core}. */
public record Formalizacion(
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
        List<FormalizacionItem> items,
        LocalDateTime created,
        LocalDateTime updated) {
}
