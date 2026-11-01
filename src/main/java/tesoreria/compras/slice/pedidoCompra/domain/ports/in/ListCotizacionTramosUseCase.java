package tesoreria.compras.slice.pedidoCompra.domain.ports.in;

import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;

import java.util.List;

public interface ListCotizacionTramosUseCase {

    List<CotizacionTramo> listar();
}
