package tesoreria.compras.slice.ubicacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;
import tesoreria.compras.slice.ubicacion.infrastructure.web.dto.UbicacionResponse;

@Component
public class UbicacionDtoMapper {

    public UbicacionResponse toResponse(Ubicacion ubicacion) {
        return new UbicacionResponse(
                ubicacion.ubicacionId(),
                ubicacion.nombre(),
                ubicacion.dependenciaId(),
                ubicacion.geograficaId()
        );
    }
}
