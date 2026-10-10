package tesoreria.compras.slice.articulo.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloConflictException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloNotFoundException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloSourceUnavailableException;
import tesoreria.compras.slice.articulo.domain.exception.ArticuloValidationException;
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

    @Override
    public PaginatedResponse<Articulo> getPaginatedByTipo(String tipo, int page, int size) {
        try {
            PaginatedResponse<CoreArticuloResponse> response =
                    coreArticuloFeignClient.getPaginatedByTipo(tipo, new PageRequest(page, size));
            List<Articulo> data = response.data() == null ? List.of()
                    : response.data().stream().map(this::toDomain).toList();
            return new PaginatedResponse<>(data, response.totalElements(), response.totalPages(),
                    response.currentPage(), response.pageSize());
        } catch (FeignException exception) {
            throw new ArticuloSourceUnavailableException(exception);
        }
    }

    @Override
    public Articulo getNewArticulo() {
        try {
            return toDomain(coreArticuloFeignClient.getNewArticulo());
        } catch (FeignException exception) {
            throw new ArticuloSourceUnavailableException(exception);
        }
    }

    @Override
    public Articulo createArticulo(Articulo articulo) {
        try {
            return toDomain(coreArticuloFeignClient.createArticulo(articulo));
        } catch (FeignException exception) {
            throw translate(exception, articulo == null ? null : articulo.articuloId());
        }
    }

    @Override
    public Articulo updateArticulo(Long articuloId, Articulo articulo) {
        try {
            return toDomain(coreArticuloFeignClient.updateArticulo(articuloId, articulo));
        } catch (FeignException exception) {
            throw translate(exception, articuloId);
        }
    }

    @Override
    public void deleteArticulo(Long articuloId) {
        try {
            coreArticuloFeignClient.deleteArticulo(articuloId);
        } catch (FeignException exception) {
            throw translate(exception, articuloId);
        }
    }

    private RuntimeException translate(FeignException exception, Long articuloId) {
        int status = exception.status();
        if (status == 404) {
            return new ArticuloNotFoundException(articuloId, exception);
        }
        if (status == 409) {
            return new ArticuloConflictException(exception);
        }
        if (status >= 400 && status < 500) {
            return new ArticuloValidationException(exception);
        }
        return new ArticuloSourceUnavailableException(exception);
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
