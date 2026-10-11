package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoResponse;

@Component
public class CotizacionTramoDtoMapper {

    public CotizacionTramo toDomain(CotizacionTramoRequest request) {
        if (request == null) {
            return null;
        }
        return new CotizacionTramo(
                null,
                request.nombre(),
                request.topeMultiplico(),
                request.cantidadMinima(),
                request.topeMultiplico() == null,
                request.activo());
    }

    public CotizacionTramoResponse toResponse(CotizacionTramo domain) {
        if (domain == null) {
            return null;
        }
        return new CotizacionTramoResponse(
                domain.cotizacionTramoId(),
                domain.nombre(),
                domain.topeMultiplico(),
                domain.cantidadMinima(),
                domain.abierto(),
                domain.activo());
    }
}
