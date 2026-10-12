package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.FormalizacionGateway;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoreCompraFormalizacionFeignClientAdapter implements FormalizacionGateway {

    private final CoreCompraFormalizacionFeignClient client;

    @Override
    public List<FormalizacionPendiente> pendientes(Integer ejercicioId) {
        try {
            return client.pendientes(ejercicioId).stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<Formalizacion> listar(FormalizacionFiltro filtro) {
        try {
            List<CoreCompraFormalizacionResponse> responses = esVacio(filtro)
                    ? client.findAll()
                    : client.search(new CoreCompraFormalizacionCriteriaRequest(filtro.ejercicioId(),
                            filtro.proveedorId(), filtro.fechaDesde(), filtro.fechaHasta()));
            return responses.stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Formalizacion getById(Long compraFormalizacionId) {
        try {
            return toDomain(client.findById(compraFormalizacionId));
        } catch (FeignException.NotFound exception) {
            throw new FormalizacionNotFoundException(compraFormalizacionId);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Optional<Formalizacion> porCotizacion(Long compraCotizacionId) {
        try {
            return Optional.ofNullable(toDomain(client.porCotizacion(compraCotizacionId)));
        } catch (FeignException.NotFound exception) {
            return Optional.empty();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Formalizacion formalizar(FormalizacionSolicitud solicitud, Long usuarioId) {
        try {
            return toDomain(client.formalizar(toRequest(solicitud, usuarioId)));
        } catch (FeignException.NotFound exception) {
            throw new FormalizacionNotFoundException(
                    "El proceso de cotización no tiene una adjudicación válida para formalizar");
        } catch (FeignException.Conflict exception) {
            throw new FormalizacionConflictoException(
                    "La Orden de Compra no puede emitirse en el estado actual del proceso");
        } catch (FeignException.BadRequest exception) {
            throw new FormalizacionInvalidaException("Los datos de la Orden de Compra son inválidos");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private boolean esVacio(FormalizacionFiltro filtro) {
        return filtro == null || (filtro.ejercicioId() == null && filtro.proveedorId() == null
                && filtro.fechaDesde() == null && filtro.fechaHasta() == null);
    }

    private CoreCompraFormalizacionRequest toRequest(FormalizacionSolicitud solicitud, Long usuarioId) {
        return new CoreCompraFormalizacionRequest(solicitud.compraCotizacionId(), solicitud.numeroExterno(),
                solicitud.moneda(), solicitud.monto(), solicitud.condicionPago(), solicitud.plazoEntrega(),
                solicitud.garantia(), solicitud.objeto(), solicitud.observacion(),
                usuarioId != null ? usuarioId.intValue() : null);
    }

    private Formalizacion toDomain(CoreCompraFormalizacionResponse response) {
        if (response == null) return null;
        List<FormalizacionItem> items = response.items() == null ? List.of()
                : response.items().stream().map(this::toDomain).toList();
        return new Formalizacion(response.compraFormalizacionId(), response.compraAdjudicacionId(), response.numero(),
                response.numeroExterno(), response.ejercicioId(), response.fecha(), response.proveedorId(),
                response.moneda(), response.monto(), response.condicionPago(), response.plazoEntrega(),
                response.garantia(), response.objeto(), response.observacion(), response.usuarioId(), items,
                response.created(), response.updated());
    }

    private FormalizacionItem toDomain(CoreCompraFormalizacionItemResponse response) {
        if (response == null) return null;
        return new FormalizacionItem(response.compraFormalizacionItemId(), response.compraCotizacionItemId(),
                response.compraPedidoItemId(), response.orden(), response.cantidad(), response.unidad(),
                response.descripcion(), response.especificaciones(), response.precioUnitario(), response.subtotal(),
                response.marca());
    }

    private FormalizacionPendiente toDomain(CoreCompraFormalizacionPendienteResponse response) {
        if (response == null) return null;
        List<FormalizacionItem> items = response.items() == null ? List.of()
                : response.items().stream().map(this::toDomain).toList();
        return new FormalizacionPendiente(response.compraCotizacionId(), response.numero(), response.ejercicioId(),
                response.compraAdjudicacionId(), response.compraCotizacionProveedorId(), response.proveedorId(),
                response.monto(), response.moneda(), response.condicionPago(), response.plazoEntrega(),
                response.garantia(), items);
    }
}
