package tesoreria.compras.slice.pedidoCompra.domain.ports.out;

import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Configuración de tramos de cotizaciones mínimas (resuelta en {@code core}).
 */
public interface CotizacionGateway {

    List<CotizacionTramo> listar();

    CotizacionTramo crear(CotizacionTramo tramo);

    CotizacionTramo actualizar(Long cotizacionTramoId, CotizacionTramo tramo);

    void eliminar(Long cotizacionTramoId);

    /**
     * Resuelve el mínimo de cotizaciones para un monto de un ejercicio. Lanza
     * {@link tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionesNoConfiguradasException}
     * cuando la configuración no lo permite.
     */
    Integer resolverMinimo(Integer ejercicioId, BigDecimal monto);
}
