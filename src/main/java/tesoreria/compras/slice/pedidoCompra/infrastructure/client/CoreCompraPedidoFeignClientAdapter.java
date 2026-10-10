package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraEstadoInvalidoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraItem;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CompraPedidoGateway;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoreCompraPedidoFeignClientAdapter implements CompraPedidoGateway {

    private final CoreCompraPedidoFeignClient coreCompraPedidoFeignClient;

    @Override
    public PedidoCompra crear(PedidoCompra pedido) {
        try {
            return toDomain(coreCompraPedidoFeignClient.crear(pedido));
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra actualizar(Integer compraPedidoId, PedidoCompra pedido) {
        try {
            return toDomain(coreCompraPedidoFeignClient.actualizar(compraPedidoId, pedido));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra enviar(Integer compraPedidoId, Long usuarioId) {
        try {
            return toDomain(coreCompraPedidoFeignClient.enviar(compraPedidoId,
                    new CoreEnviarCompraPedidoRequest(toInteger(usuarioId))));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra aprobar(Integer compraPedidoId, Long autorizanteId) {
        try {
            return toDomain(coreCompraPedidoFeignClient.aprobar(compraPedidoId,
                    new CoreAprobarCompraPedidoRequest(toInteger(autorizanteId))));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra rechazar(Integer compraPedidoId, Long autorizanteId, String motivo) {
        try {
            return toDomain(coreCompraPedidoFeignClient.rechazar(compraPedidoId,
                    new CoreRechazarCompraPedidoRequest(toInteger(autorizanteId), motivo)));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra descartar(Integer compraPedidoId, Long usuarioId, String motivo) {
        try {
            return toDomain(coreCompraPedidoFeignClient.descartar(compraPedidoId,
                    new CoreDescartarCompraPedidoRequest(toInteger(usuarioId), motivo)));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra estimar(Integer compraPedidoId, Long usuarioId, BigDecimal monto, String fuente) {
        try {
            return toDomain(coreCompraPedidoFeignClient.estimar(compraPedidoId,
                    new CoreEstimarCompraPedidoRequest(monto, fuente, toInteger(usuarioId))));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra autorizarPresupuesto(Integer compraPedidoId, Long usuarioId) {
        try {
            return toDomain(coreCompraPedidoFeignClient.autorizarPresupuesto(compraPedidoId,
                    new CoreAutorizarPresupuestoCompraPedidoRequest(toInteger(usuarioId))));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra rechazarPresupuesto(Integer compraPedidoId, Long usuarioId, String motivo) {
        try {
            return toDomain(coreCompraPedidoFeignClient.rechazarPresupuesto(compraPedidoId,
                    new CoreRechazarPresupuestoCompraPedidoRequest(toInteger(usuarioId), motivo)));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException.Conflict exception) {
            throw new PedidoCompraEstadoInvalidoException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public PedidoCompra getById(Integer compraPedidoId) {
        try {
            return toDomain(coreCompraPedidoFeignClient.getById(compraPedidoId));
        } catch (FeignException.NotFound exception) {
            throw new PedidoCompraNotFoundException(compraPedidoId, exception);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<PedidoCompra> listarPorSolicitante(Long solicitanteId) {
        try {
            return coreCompraPedidoFeignClient.listarPorSolicitante(
                    new CoreCompraPedidoSearchRequest(null, toInteger(solicitanteId), null, null, null, null)).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<PedidoCompra> listar(PedidoCompraFiltro filtro) {
        try {
            return coreCompraPedidoFeignClient.search(new CoreCompraPedidoSearchRequest(
                            filtro.estado(), filtro.solicitanteId(), filtro.dependenciaId(),
                            filtro.dependenciaIds(), filtro.fechaDesde(), filtro.fechaHasta())).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private Integer toInteger(Long userId) {
        return userId == null ? null : userId.intValue();
    }

    private PedidoCompra toDomain(CoreCompraPedidoResponse response) {
        return new PedidoCompra(
                response.compraPedidoId(),
                response.numero(),
                response.ejercicioId(),
                response.fecha(),
                response.estado(),
                response.autorizanteId(),
                response.solicitanteId(),
                response.dependenciaId(),
                response.facultadId(),
                response.geograficaId(),
                response.necesidad(),
                response.fechaRequerida(),
                response.urgente(),
                response.urgenciaMotivo(),
                response.montoConocido(),
                response.montoEstimado(),
                response.fuenteEstimacion(),
                response.fechaEnvio(),
                response.rechazoMotivo(),
                response.descartadoMotivo(),
                toDomainItems(response.items()));
    }

    private List<PedidoCompraItem> toDomainItems(List<CoreCompraPedidoItemResponse> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new PedidoCompraItem(
                        item.compraPedidoItemId(),
                        item.compraPedidoId(),
                        item.orden(),
                        item.cantidad(),
                        item.unidad(),
                        item.descripcion(),
                        item.especificaciones(),
                        item.referenciaWeb()))
                .toList();
    }
}
