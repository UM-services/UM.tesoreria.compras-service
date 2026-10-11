package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionTramoNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionesNoConfiguradasException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionTramo;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionGateway;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreCotizacionTramoFeignClientAdapter implements CotizacionGateway {

    private final CoreCotizacionTramoFeignClient coreCotizacionTramoFeignClient;

    @Override
    public List<CotizacionTramo> listar() {
        try {
            return coreCotizacionTramoFeignClient.findAll().stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Integer resolverMinimo(Integer ejercicioId, BigDecimal monto) {
        try {
            CoreCotizacionMinimoResponse response = coreCotizacionTramoFeignClient.minimo(ejercicioId, monto);
            return response == null ? null : response.cantidadMinima();
        } catch (FeignException.Conflict exception) {
            throw new CotizacionesNoConfiguradasException();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionTramo crear(CotizacionTramo tramo) {
        try {
            return toDomain(coreCotizacionTramoFeignClient.create(toRequest(tramo)));
        } catch (FeignException.BadRequest exception) {
            throw new CotizacionTramoInvalidaException();
        } catch (FeignException.Conflict exception) {
            throw new CotizacionTramoConflictoException("La configuración del tramo entra en conflicto con otra existente");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionTramo actualizar(Long cotizacionTramoId, CotizacionTramo tramo) {
        try {
            return toDomain(coreCotizacionTramoFeignClient.update(cotizacionTramoId, toRequest(tramo)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionTramoNotFoundException(cotizacionTramoId);
        } catch (FeignException.BadRequest exception) {
            throw new CotizacionTramoInvalidaException();
        } catch (FeignException.Conflict exception) {
            throw new CotizacionTramoConflictoException("La configuración del tramo entra en conflicto con otra existente");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public void eliminar(Long cotizacionTramoId) {
        try {
            coreCotizacionTramoFeignClient.delete(cotizacionTramoId);
        } catch (FeignException.NotFound exception) {
            throw new CotizacionTramoNotFoundException(cotizacionTramoId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionTramoConflictoException("No se puede eliminar el tramo " + cotizacionTramoId);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private CoreCotizacionTramoRequest toRequest(CotizacionTramo tramo) {
        return new CoreCotizacionTramoRequest(
                tramo.nombre(), tramo.topeMultiplico(), tramo.cantidadMinima(), tramo.activo());
    }

    private CotizacionTramo toDomain(CoreCotizacionTramoResponse response) {
        if (response == null) {
            return null;
        }
        return new CotizacionTramo(
                response.cotizacionTramoId(),
                response.nombre(),
                response.topeMultiplico(),
                response.cantidadMinima(),
                response.abierto(),
                response.activo());
    }
}
