package tesoreria.compras.ordencompra.infrastructure.web.dto;
import java.math.BigDecimal; public record OrdenCompraItemResponse(Integer articuloId,String descripcion,BigDecimal cantidad,BigDecimal precioUnitario,Long imputacionId,BigDecimal subtotal) { }
