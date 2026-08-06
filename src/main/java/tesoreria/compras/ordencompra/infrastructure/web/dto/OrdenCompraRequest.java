package tesoreria.compras.ordencompra.infrastructure.web.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.List;
public record OrdenCompraRequest(@NotNull LocalDate fechaEmision,@NotNull Integer proveedorId,@NotNull Integer sedeId,@Size(max=1000) String observaciones,@NotEmpty List<@Valid OrdenCompraItemRequest> items) { }
