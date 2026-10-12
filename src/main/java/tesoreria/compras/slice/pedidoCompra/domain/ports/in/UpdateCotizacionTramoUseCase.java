package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;

public interface UpdateCotizacionTramoUseCase {

    CotizacionTramo actualizar(Long cotizacionTramoId, CotizacionTramo tramo);
}
