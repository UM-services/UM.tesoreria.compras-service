package tesoreria.compras.slice.ubicacion.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;
import tesoreria.compras.slice.ubicacion.domain.ports.in.GetUbicacionesUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final GetUbicacionesUseCase getUbicacionesUseCase;

    public List<Ubicacion> getUbicaciones() {
        return getUbicacionesUseCase.getUbicaciones();
    }
}
