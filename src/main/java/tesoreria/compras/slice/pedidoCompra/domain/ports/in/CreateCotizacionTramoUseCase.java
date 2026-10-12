package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;

public interface CreateCotizacionTramoUseCase {

    CotizacionTramo crear(CotizacionTramo tramo);
}
