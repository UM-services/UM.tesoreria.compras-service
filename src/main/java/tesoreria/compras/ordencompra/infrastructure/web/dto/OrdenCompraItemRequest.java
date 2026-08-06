package tesoreria.compras.ordencompra.infrastructure.web.dto;
import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record OrdenCompraItemRequest(@NotNull Integer articuloId,@Size(max=255) String descripcion,@NotNull @DecimalMin(value="0.0001") BigDecimal cantidad,@NotNull @DecimalMin("0.0") BigDecimal precioUnitario,@NotNull Long imputacionId) { }
