package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.CuentaRefResponse;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloResponse;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionRefResponse;

@Component
public class UbicacionArticuloDtoMapper {

    public UbicacionArticuloResponse toResponse(UbicacionArticulo ubicacionArticulo) {
        UbicacionRefResponse ubicacion = ubicacionArticulo.ubicacionId() == null && ubicacionArticulo.ubicacionNombre() == null
                ? null
                : new UbicacionRefResponse(ubicacionArticulo.ubicacionId(), ubicacionArticulo.ubicacionNombre());
        CuentaRefResponse cuenta = ubicacionArticulo.cuentaNombre() == null && ubicacionArticulo.numeroCuenta() == null
                ? null
                : new CuentaRefResponse(ubicacionArticulo.numeroCuenta(), ubicacionArticulo.cuentaNombre());
        return new UbicacionArticuloResponse(
                ubicacionArticulo.ubicacionArticuloId(),
                ubicacionArticulo.ubicacionId(),
                ubicacionArticulo.articuloId(),
                ubicacionArticulo.numeroCuenta(),
                ubicacion,
                cuenta
        );
    }

    public UbicacionArticulo toDomain(UbicacionArticuloRequest request) {
        return new UbicacionArticulo(
                null,
                request.ubicacionId(),
                request.articuloId(),
                request.numeroCuenta(),
                null,
                null
        );
    }
}
