package tesoreria.compras.slice.ubicacion.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacion.domain.exception.UbicacionSourceUnavailableException;
import tesoreria.compras.slice.ubicacion.domain.model.Ubicacion;
import tesoreria.compras.slice.ubicacion.domain.ports.out.UbicacionGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreUbicacionFeignClientAdapter implements UbicacionGateway {

    private final CoreUbicacionFeignClient coreUbicacionFeignClient;

    @Override
    public List<Ubicacion> getUbicaciones() {
        try {
            return coreUbicacionFeignClient.getUbicaciones().stream()
                    .map(response -> new Ubicacion(
                            response.ubicacionId(),
                            response.nombre(),
                            response.dependenciaId(),
                            response.geograficaId()))
                    .toList();
        } catch (FeignException exception) {
            throw new UbicacionSourceUnavailableException(exception);
        }
    }
}
