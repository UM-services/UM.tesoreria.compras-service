package tesoreria.compras.ordencompra.domain.ports.in;
import tesoreria.compras.ordencompra.domain.model.*; import java.time.LocalDate; import java.util.List;
public interface UpdateOrdenCompraUseCase { OrdenCompra update(Long id, LocalDate fecha, Integer proveedorId, Integer sedeId, String observaciones, List<OrdenCompraItem> items); }
