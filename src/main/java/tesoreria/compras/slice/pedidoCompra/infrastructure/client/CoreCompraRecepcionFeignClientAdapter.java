package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.exception.PedidoCompraSourceUnavailableException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItemSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendienteItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.RecepcionGateway;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoreCompraRecepcionFeignClientAdapter implements RecepcionGateway {

    private final CoreCompraRecepcionFeignClient client;

    @Override
    public List<RecepcionPendiente> pendientes(Integer ejercicioId) {
        try {
            return client.pendientes(ejercicioId).stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public List<Recepcion> listar(RecepcionFiltro filtro) {
        try {
            List<CoreCompraRecepcionResponse> responses = esVacio(filtro)
                    ? client.findAll()
                    : client.search(new CoreCompraRecepcionCriteriaRequest(filtro.ejercicioId(),
                            filtro.compraFormalizacionId(), filtro.resultado(), filtro.fechaDesde(),
                            filtro.fechaHasta()));
            return responses.stream().map(this::toDomain).toList();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Recepcion getById(Long compraRecepcionId) {
        try {
            return toDomain(client.findById(compraRecepcionId));
        } catch (FeignException.NotFound exception) {
            throw new RecepcionNotFoundException("No existe la recepción con id: " + compraRecepcionId);
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Optional<RecepcionPendiente> porFormalizacion(Long compraFormalizacionId) {
        try {
            return Optional.ofNullable(toDomain(client.porFormalizacion(compraFormalizacionId)));
        } catch (FeignException.NotFound exception) {
            return Optional.empty();
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public Recepcion registrar(RecepcionSolicitud solicitud, Long usuarioId) {
        try {
            return toDomain(client.registrar(toRequest(solicitud, usuarioId)));
        } catch (FeignException.NotFound exception) {
            throw new RecepcionNotFoundException(
                    "No existe la Orden de Compra " + (solicitud == null ? null : solicitud.compraFormalizacionId()));
        } catch (FeignException.BadRequest exception) {
            throw new RecepcionInvalidaException("Los datos de la recepción son inválidos");
        } catch (FeignException.Conflict exception) {
            throw new RecepcionConflictoException(
                    "La recepción no puede registrarse en el estado actual de la Orden de Compra");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    @Override
    public void cerrar(Long compraFormalizacionId, String motivo, Long usuarioId) {
        try {
            client.cerrar(compraFormalizacionId, new CoreCerrarCompraRecepcionRequest(motivo,
                    usuarioId != null ? usuarioId.intValue() : null));
        } catch (FeignException.NotFound exception) {
            throw new RecepcionNotFoundException("No existe la Orden de Compra " + compraFormalizacionId);
        } catch (FeignException.BadRequest exception) {
            throw new RecepcionInvalidaException("El cierre de la recepción requiere un motivo");
        } catch (FeignException.Conflict exception) {
            throw new RecepcionConflictoException(
                    "La Orden de Compra no puede cerrarse en el estado actual");
        } catch (FeignException exception) {
            throw new PedidoCompraSourceUnavailableException(exception);
        }
    }

    private boolean esVacio(RecepcionFiltro filtro) {
        return filtro == null || (filtro.ejercicioId() == null && filtro.compraFormalizacionId() == null
                && (filtro.resultado() == null || filtro.resultado().isBlank())
                && filtro.fechaDesde() == null && filtro.fechaHasta() == null);
    }

    private CoreCompraRecepcionRequest toRequest(RecepcionSolicitud solicitud, Long usuarioId) {
        return new CoreCompraRecepcionRequest(solicitud.compraFormalizacionId(), solicitud.fecha(),
                solicitud.remito(), solicitud.patrimonio(), solicitud.observacion(),
                usuarioId != null ? usuarioId.intValue() : null,
                solicitud.items() == null ? List.of()
                        : solicitud.items().stream().map(this::toRequest).toList());
    }

    private CoreCompraRecepcionItemRequest toRequest(RecepcionItemSolicitud item) {
        return new CoreCompraRecepcionItemRequest(item.compraFormalizacionItemId(), item.orden(),
                item.cantidadRecibida(), item.resultado(), item.observacion());
    }

    private Recepcion toDomain(CoreCompraRecepcionResponse response) {
        if (response == null) return null;
        List<RecepcionItem> items = response.items() == null ? List.of()
                : response.items().stream().map(this::toDomain).toList();
        return new Recepcion(response.compraRecepcionId(), response.compraFormalizacionId(), response.numero(),
                response.ejercicioId(), response.fecha(), response.remito(), response.resultado(),
                response.patrimonio(), response.observacion(), response.usuarioId(), items, response.created(),
                response.updated());
    }

    private RecepcionItem toDomain(CoreCompraRecepcionItemResponse response) {
        if (response == null) return null;
        return new RecepcionItem(response.compraRecepcionItemId(), response.compraFormalizacionItemId(),
                response.orden(), response.cantidadRecibida(), response.resultado(), response.observacion());
    }

    private RecepcionPendiente toDomain(CoreCompraRecepcionPendienteResponse response) {
        if (response == null) return null;
        List<RecepcionPendienteItem> items = response.items() == null ? List.of()
                : response.items().stream().map(this::toDomain).toList();
        List<Recepcion> recepciones = response.recepciones() == null ? List.of()
                : response.recepciones().stream().map(this::toDomain).toList();
        return new RecepcionPendiente(response.compraFormalizacionId(), response.numero(), response.numeroExterno(),
                response.ejercicioId(), response.proveedorId(), response.fecha(), response.monto(),
                response.moneda(), items, recepciones, response.completo());
    }

    private RecepcionPendienteItem toDomain(CoreCompraRecepcionPendienteItemResponse response) {
        if (response == null) return null;
        return new RecepcionPendienteItem(response.compraFormalizacionItemId(), response.compraPedidoItemId(),
                response.orden(), response.cantidad(), response.recibido(), response.saldo(), response.unidad(),
                response.descripcion(), response.especificaciones(), response.precioUnitario(), response.subtotal(),
                response.marca(), response.completo());
    }
}
