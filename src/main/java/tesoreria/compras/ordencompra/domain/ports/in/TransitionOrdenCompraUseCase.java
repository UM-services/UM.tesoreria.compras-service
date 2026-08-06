package tesoreria.compras.ordencompra.domain.ports.in;
import tesoreria.compras.ordencompra.domain.model.*;
public interface TransitionOrdenCompraUseCase { OrdenCompra transition(Long id, OrdenCompraEstado estado); }
