package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionProcesoGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoreCompraCotizacionFeignClientAdapter implements CotizacionProcesoGateway {

    private final CoreCompraCotizacionFeignClient client;

    @Override
    public List<CotizacionPendiente> pendientes(Integer ejercicioId, List<Integer> dependenciaIds) {
        try {
            return client.pendientes(ejercicioId, dependenciaIds == null ? List.of() : dependenciaIds).stream()
                    .map(this::toDomain)
                    .toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<CotizacionProcesoProveedorPendiente> respuestasPendientes(Integer ejercicioId) {
        try {
            return client.proveedoresPendientes(ejercicioId).stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<CotizacionProceso> listar(CotizacionProcesoFiltro filtro) {        try {
            List<CoreCompraCotizacionResponse> responses = esVacio(filtro)
                    ? client.findAll()
                    : client.search(new CoreCompraCotizacionCriteriaRequest(filtro.estado(), filtro.ejercicioId(),
                            filtro.fechaDesde(), filtro.fechaHasta()));
            return responses.stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private boolean esVacio(CotizacionProcesoFiltro filtro) {
        return filtro == null || (filtro.estado() == null && filtro.ejercicioId() == null
                && filtro.fechaDesde() == null && filtro.fechaHasta() == null);
    }

    @Override
    public CotizacionProceso getById(Long compraCotizacionId) {
        try {
            return toDomain(client.findById(compraCotizacionId));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso crear(CotizacionProceso proceso) {
        try {
            return toDomain(client.create(toRequest(proceso)));
        } catch (FeignException.BadRequest exception) {
            throw new CotizacionProcesoInvalidaException("Los datos del proceso de cotización son inválidos");
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("El proceso de cotización entra en conflicto");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso actualizar(Long compraCotizacionId, CotizacionProceso proceso) {
        try {
            return toDomain(client.update(compraCotizacionId, toRequest(proceso)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.BadRequest exception) {
            throw new CotizacionProcesoInvalidaException("Los datos del proceso de cotización son inválidos");
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("Sólo un borrador puede editarse");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso enviar(Long compraCotizacionId) {
        try {
            return toDomain(client.enviar(compraCotizacionId));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException(
                    "El proceso no puede enviarse (faltan ítems, proveedores o el mínimo de cotizaciones)");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso anular(Long compraCotizacionId, String motivo) {
        try {
            return toDomain(client.anular(compraCotizacionId, new CoreMotivoRequest(motivo)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("El proceso no puede anularse en su estado actual");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    // ---- Etapa 6C ----

    @Override
    public CotizacionProcesoProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                         CotizacionProcesoProveedor respuesta) {
        try {
            return toDomain(client.registrarRespuesta(compraCotizacionId, compraCotizacionProveedorId,
                    toRespuestaRequest(respuesta)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("El proceso no está enviado");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProcesoProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                     String observacion) {
        try {
            return toDomain(client.marcarNoCotiza(compraCotizacionId, compraCotizacionProveedorId,
                    new CoreMotivoRequest(observacion)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("El documento de proveedor no puede marcarse");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso comparacion(Long compraCotizacionId) {
        try {
            return toDomain(client.comparacion(compraCotizacionId));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento) {
        try {
            return toDomain(client.recomendar(compraCotizacionId,
                    new CoreRecomendarCompraCotizacionRequest(compraCotizacionProveedorId, fundamento)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException(
                    "No se puede recomendar (faltan respuestas o el proceso no está enviado)");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public CotizacionProceso cerrar(Long compraCotizacionId) {
        try {
            return toDomain(client.cerrar(compraCotizacionId));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("Sólo un proceso recomendado puede cerrarse");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    // ---- Etapa 7: adjudicación ----

    @Override
    public CotizacionAdjudicacion adjudicar(Long compraCotizacionId, String decision, Long compraCotizacionProveedorId,
                                            Integer proveedorAdjudicadoId, BigDecimal monto, String fundamento,
                                            Long autoridadUsuarioId, BigDecimal limiteAplicado) {
        try {
            return toDomain(client.adjudicar(compraCotizacionId, new CoreAdjudicarCompraCotizacionRequest(decision,
                    compraCotizacionProveedorId, proveedorAdjudicadoId, monto, fundamento, autoridadUsuarioId,
                    limiteAplicado)));
        } catch (FeignException.NotFound exception) {
            throw new CotizacionProcesoNotFoundException(compraCotizacionId);
        } catch (FeignException.Conflict exception) {
            throw new CotizacionProcesoConflictoException("El proceso no está recomendado para adjudicar");
        } catch (FeignException.BadRequest exception) {
            throw new CotizacionProcesoInvalidaException("Los datos de la adjudicación son inválidos");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Optional<CotizacionAdjudicacion> adjudicacion(Long compraCotizacionId) {
        try {
            return Optional.ofNullable(toDomain(client.adjudicacion(compraCotizacionId)));
        } catch (FeignException.NotFound exception) {
            return Optional.empty();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    // ---- Mapeo ----

    private CoreCompraCotizacionRequest toRequest(CotizacionProceso proceso) {
        List<CoreCompraCotizacionItemRequest> items = proceso.items() == null ? List.of()
                : proceso.items().stream()
                        .map(i -> new CoreCompraCotizacionItemRequest(i.compraPedidoItemId(), i.orden(), i.cantidad()))
                        .toList();
        List<CoreCompraCotizacionProveedorRequest> proveedores = proceso.proveedores() == null ? List.of()
                : proceso.proveedores().stream()
                        .map(p -> new CoreCompraCotizacionProveedorRequest(p.proveedorId(), p.email(), p.observacion()))
                        .toList();
        return new CoreCompraCotizacionRequest(proceso.ejercicioId(), proceso.fecha(), proceso.observacion(),
                proceso.fechaLimiteRespuesta(), proceso.montoEstimado(), proceso.cantidadMinima(),
                proceso.excepcionMotivo(), proceso.usuarioId(), items, proveedores);
    }

    private CoreCompraCotizacionRespuestaRequest toRespuestaRequest(CotizacionProcesoProveedor proveedor) {
        List<CoreCompraCotizacionProveedorItemRequest> items = proveedor.items() == null ? List.of()
                : proveedor.items().stream()
                        .map(i -> new CoreCompraCotizacionProveedorItemRequest(i.compraCotizacionItemId(), i.cotiza(),
                                i.precioUnitario(), i.subtotal(), i.marca(), i.observacion()))
                        .toList();
        return new CoreCompraCotizacionRespuestaRequest(proveedor.fechaRespuesta(), proveedor.moneda(),
                proveedor.total(), proveedor.plazoEntrega(), proveedor.condicionPago(), proveedor.garantia(),
                proveedor.validezOferta(), items);
    }

    private CotizacionProceso toDomain(CoreCompraCotizacionResponse response) {
        if (response == null) return null;
        List<CotizacionProcesoItem> items = response.items() == null ? List.of()
                : response.items().stream()
                        .map(i -> new CotizacionProcesoItem(i.compraCotizacionItemId(), i.compraPedidoItemId(),
                                i.orden(), i.cantidad()))
                        .toList();
        List<CotizacionProcesoProveedor> proveedores = response.proveedores() == null ? List.of()
                : response.proveedores().stream().map(this::toDomain).toList();
        return new CotizacionProceso(response.compraCotizacionId(), response.numero(), response.ejercicioId(),
                response.fecha(), response.estado(), response.observacion(), response.fechaLimiteRespuesta(),
                response.montoEstimado(), response.cantidadMinima(), response.excepcionMotivo(),
                response.fechaEnvio(), response.proveedorRecomendadoId(), response.fundamentoRecomendacion(),
                response.fechaRecomendacion(), response.usuarioId(), items, proveedores);
    }

    private CotizacionProcesoProveedor toDomain(CoreCompraCotizacionProveedorResponse response) {
        if (response == null) return null;
        List<CotizacionProcesoProveedorItem> items = response.items() == null ? List.of()
                : response.items().stream()
                        .map(i -> new CotizacionProcesoProveedorItem(i.compraCotizacionProveedorItemId(),
                                i.compraCotizacionItemId(), i.cotiza(), i.precioUnitario(), i.subtotal(), i.marca(),
                                i.observacion()))
                        .toList();
        return new CotizacionProcesoProveedor(response.compraCotizacionProveedorId(), response.proveedorId(),
                response.email(), response.estado(), response.fechaEnvio(), response.observacion(),
                response.fechaRespuesta(), response.moneda(), response.total(), response.plazoEntrega(),
                response.condicionPago(), response.garantia(), response.validezOferta(), items);
    }

    private CotizacionPendiente toDomain(CoreCompraCotizacionPendienteResponse response) {
        return new CotizacionPendiente(response.compraPedidoItemId(), response.compraPedidoId(),
                response.pedidoNumero(), response.dependenciaId(), response.orden(), response.cantidad(),
                response.unidad(), response.descripcion(), response.especificaciones(), response.referenciaWeb());
    }

    private CotizacionProcesoProveedorPendiente toDomain(CoreCompraCotizacionProveedorPendienteResponse response) {
        if (response == null) return null;
        List<CotizacionProcesoItem> items = response.items() == null ? List.of()
                : response.items().stream()
                        .map(i -> new CotizacionProcesoItem(i.compraCotizacionItemId(), i.compraPedidoItemId(),
                                i.orden(), i.cantidad()))
                        .toList();
        return new CotizacionProcesoProveedorPendiente(response.compraCotizacionId(), response.numero(),
                response.ejercicioId(), response.fechaLimiteRespuesta(), toDomain(response.proveedor()), items);
    }

    private CotizacionAdjudicacion toDomain(CoreCompraAdjudicacionResponse response) {
        if (response == null) return null;
        return new CotizacionAdjudicacion(response.compraAdjudicacionId(), response.compraCotizacionId(),
                response.ejercicioId(), response.compraCotizacionProveedorId(), response.proveedorAdjudicadoId(),
                response.monto(), response.decision(), response.fundamento(), response.autoridadUsuarioId(),
                response.limiteAplicado(), response.fecha());
    }
}
