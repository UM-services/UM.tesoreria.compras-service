package tesoreria.compras.ordencompra.domain.ports.in;
import tesoreria.compras.ordencompra.domain.model.*; import java.util.List;
public interface ListOrdenCompraUseCase { List<OrdenCompra> list(OrdenCompraCriteria criteria); }
