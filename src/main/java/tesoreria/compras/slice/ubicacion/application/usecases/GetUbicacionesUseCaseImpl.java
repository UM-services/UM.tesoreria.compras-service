package tesoreria.compras.slice.ubicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;
import tesoreria.compras.slice.ubicacion.domain.ports.in.GetUbicacionesUseCase;
import tesoreria.compras.slice.ubicacion.domain.ports.out.UbicacionGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUbicacionesUseCaseImpl implements GetUbicacionesUseCase {

    private final UbicacionGateway ubicacionGateway;

    @Override
    public List<Ubicacion> getUbicaciones() {
        return ubicacionGateway.getUbicaciones();
    }
}
