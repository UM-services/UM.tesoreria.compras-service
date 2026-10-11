package tesoreria.compras.slice.ubicacionArticulo.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloConflictException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloSourceUnavailableException;
import tesoreria.compras.slice.ubicacionArticulo.domain.exception.UbicacionArticuloValidationException;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;
import tesoreria.compras.slice.ubicacionArticulo.domain.ports.out.UbicacionArticuloGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreUbicacionArticuloFeignClientAdapter implements UbicacionArticuloGateway {

    private final CoreUbicacionArticuloFeignClient coreUbicacionArticuloFeignClient;

    @Override
    public List<UbicacionArticulo> getByArticulo(Long articuloId) {
        try {
            return coreUbicacionArticuloFeignClient.getByArticulo(articuloId).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new UbicacionArticuloSourceUnavailableException(exception);
        }
    }

    @Override
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        try {
            return toDomain(coreUbicacionArticuloFeignClient.save(ubicacionArticulo));
        } catch (FeignException exception) {
            throw translate(exception);
        }
    }

    private RuntimeException translate(FeignException exception) {
        int status = exception.status();
        if (status == 409) {
            return new UbicacionArticuloConflictException(exception);
        }
        if (status >= 400 && status < 500) {
            return new UbicacionArticuloValidationException(exception);
        }
        return new UbicacionArticuloSourceUnavailableException(exception);
    }

    private UbicacionArticulo toDomain(CoreUbicacionArticuloResponse response) {
        return new UbicacionArticulo(
                response.ubicacionArticuloId(),
                response.ubicacionId(),
                response.articuloId(),
                response.numeroCuenta(),
                response.ubicacion() == null ? null : response.ubicacion().nombre(),
                response.cuenta() == null ? null : response.cuenta().nombre()
        );
    }
}
