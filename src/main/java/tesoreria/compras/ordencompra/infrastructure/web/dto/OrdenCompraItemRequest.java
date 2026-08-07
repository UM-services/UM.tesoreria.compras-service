package tesoreria.compras.ordencompra.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrdenCompraItemRequest(
        @NotNull Integer articuloId,
        @Size(max = 255) String descripcion,
        @NotNull @DecimalMin(value = "0.0001") BigDecimal cantidad,
        @NotNull @DecimalMin("0.0") BigDecimal precioUnitario,
        @NotNull Long imputacionId) {
}
