package tesoreria.compras.slice.pedidoCompra.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.CreateCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.DeleteCotizacionTramoUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.ListCotizacionTramosUseCase;
import tesoreria.compras.slice.pedidoCompra.domain.ports.in.UpdateCotizacionTramoUseCase;

import java.util.List;

/**
 * Fachada de configuración de tramos de cotizaciones mínimas. El permiso
 * {@code compras.cotizaciones.configurar} lo exige el PEP declarativo del controller
 * ({@code @RequierePermiso}).
 */
@Service
@RequiredArgsConstructor
public class CotizacionTramoService {

    private final ListCotizacionTramosUseCase listCotizacionTramosUseCase;
    private final CreateCotizacionTramoUseCase createCotizacionTramoUseCase;
    private final UpdateCotizacionTramoUseCase updateCotizacionTramoUseCase;
    private final DeleteCotizacionTramoUseCase deleteCotizacionTramoUseCase;

    public List<CotizacionTramo> listar() {
        return listCotizacionTramosUseCase.listar();
    }

    public CotizacionTramo crear(CotizacionTramo tramo) {
        return createCotizacionTramoUseCase.crear(tramo);
    }

    public CotizacionTramo actualizar(Long cotizacionTramoId, CotizacionTramo tramo) {
        return updateCotizacionTramoUseCase.actualizar(cotizacionTramoId, tramo);
    }

    public void eliminar(Long cotizacionTramoId) {
        deleteCotizacionTramoUseCase.eliminar(cotizacionTramoId);
    }
}
