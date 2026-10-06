package tesoreria.compras.slice.articulo.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;
import tesoreria.compras.slice.articulo.domain.model.Articulo;
import tesoreria.compras.slice.articulo.domain.model.ArticuloSearch;
import tesoreria.compras.slice.articulo.domain.model.Cuenta;
import tesoreria.compras.slice.articulo.domain.ports.out.ArticuloGateway;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreArticuloFeignClientAdapter implements ArticuloGateway {

    private final CoreArticuloFeignClient coreArticuloFeignClient;

    @Override
    public Articulo getArticuloById(Long articuloId) {
        try {
            return toDomain(coreArticuloFeignClient.getArticuloById(articuloId));
        } catch (FeignException.NotFound exception) {
            throw new ArticuloNotFoundException(articuloId, exception);
        } catch (FeignException exception) {
            throw new ArticuloSourceUnavailableException(exception);
        }
    }

    @Override
    public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        try {
            return coreArticuloFeignClient.searchArticulos(conditions).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new ArticuloSourceUnavailableException(exception);
        }
    }

    private Articulo toDomain(CoreArticuloResponse response) {
        return new Articulo(
                response.articuloId(),
                response.nombre(),
                response.descripcion(),
                response.unidad(),
                response.precio(),
                response.inventariable(),
                response.stockMinimo(),
                response.numeroCuenta(),
                response.tipo(),
                response.directo(),
                response.habilitado(),
                toDomain(response.cuenta())
        );
    }

    private ArticuloSearch toDomain(CoreArticuloSearchResponse response) {
        return new ArticuloSearch(
                response.articuloId(),
                response.nombre(),
                response.descripcion(),
                response.unidad(),
                response.precio(),
                response.inventariable(),
                response.stockMinimo(),
                response.numeroCuenta(),
                toDomain(response.cuenta()),
                response.tipo(),
                response.directo(),
                response.habilitado(),
                response.search(),
                response.fechaAuditoria(),
                response.usuarioAuditoria()
        );
    }

    private Cuenta toDomain(CoreCuentaArticuloResponse response) {
        if (response == null) {
            return null;
        }

        return new Cuenta(
                response.numeroCuenta(),
                response.nombre(),
                response.integradora(),
                response.grado(),
                response.grado1(),
                response.grado2(),
                response.grado3(),
                response.grado4(),
                response.geograficaId(),
                response.fechaBloqueo(),
                response.visible(),
                response.cuentaContableId()
        );
    }
}
