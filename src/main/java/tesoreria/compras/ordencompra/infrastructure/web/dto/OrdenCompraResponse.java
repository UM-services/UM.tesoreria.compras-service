package tesoreria.compras.ordencompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdenCompraResponse(Long id, String numero, LocalDate fechaEmision, Integer proveedorId,
                                  Integer sedeId, String observaciones, String estado, BigDecimal total,
                                  List<OrdenCompraItemResponse> items) {
}
