package tesoreria.compras.ordencompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record OrdenCompraItemResponse(Long id, Integer articuloId, String descripcion, BigDecimal cantidad,
                                      BigDecimal precioUnitario, Long imputacionId, BigDecimal subtotal) {
}
