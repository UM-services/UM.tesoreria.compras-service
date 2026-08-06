package tesoreria.compras.ordencompra.domain.model;

import java.time.LocalDate;

public record OrdenCompraCriteria(OrdenCompraEstado estado, Integer proveedorId, Integer sedeId,
                                  LocalDate fechaDesde, LocalDate fechaHasta) { }
