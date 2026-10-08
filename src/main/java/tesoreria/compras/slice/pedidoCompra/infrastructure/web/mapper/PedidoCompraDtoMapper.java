package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraHistorial;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraResumen;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.*;

import java.util.List;

@Component
public class PedidoCompraDtoMapper {

    public PedidoCompra toDomain(PedidoCompraRequest request) {
        if (request == null) {
            return null;
        }
        return new PedidoCompra(
                null, null, null, null, null, null, null, null, null, null,
                request.necesidad(), request.fechaRequerida(), request.urgente(), request.urgenciaMotivo(),
                request.montoConocido(), request.montoEstimado(), request.fuenteEstimacion(),
                null, null, null,
                toDomainItems(request.items()));
    }

    public PedidoCompraResponse toResponse(PedidoCompra domain) {
        return build(domain, null, null);
    }

    public PedidoCompraResponse toResponse(PedidoCompraResumen resumen) {
        if (resumen == null) {
            return null;
        }
        return build(resumen.pedido(), resumen.dependenciaNombre(), resumen.solicitanteNombre());
    }

    private PedidoCompraResponse build(PedidoCompra domain, String dependenciaNombre, String solicitanteNombre) {
        if (domain == null) {
            return null;
        }
        return new PedidoCompraResponse(
                domain.compraPedidoId(), domain.numero(), domain.ejercicioId(), domain.fecha(), domain.estado(),
                domain.autorizanteId(), domain.solicitanteId(), domain.dependenciaId(), domain.facultadId(),
                domain.geograficaId(), domain.necesidad(), domain.fechaRequerida(), domain.urgente(),
                domain.urgenciaMotivo(), domain.montoConocido(), domain.montoEstimado(),
                domain.fuenteEstimacion(), domain.fechaEnvio(), domain.rechazoMotivo(), domain.descartadoMotivo(),
                dependenciaNombre, solicitanteNombre,
                toResponseItems(domain.items()));
    }

    public List<PedidoCompraHistorialResponse> toResponseHistorial(List<PedidoCompraHistorial> historial) {
        if (historial == null) {
            return List.of();
        }
        return historial.stream()
                .map(entry -> new PedidoCompraHistorialResponse(
                        entry.compraPedidoHistorialId(), entry.compraPedidoId(), entry.estado(),
                        entry.usuarioId(), entry.observacion(), entry.fecha()))
                .toList();
    }

    public ContextoInicioPedidoResponse toResponse(ContextoInicioPedido contexto) {
        if (contexto == null) {
            return null;
        }
        return new ContextoInicioPedidoResponse(
                new ContextoInicioPedidoResponse.SolicitanteResponse(
                        contexto.solicitante().userId(),
                        contexto.solicitante().nombre(),
                        contexto.solicitante().login()),
                contexto.dependencia() == null ? null : new ContextoInicioPedidoResponse.DependenciaContextoResponse(
                        contexto.dependencia().dependenciaId(),
                        contexto.dependencia().nombre(),
                        contexto.dependencia().facultadId(),
                        contexto.dependencia().facultadNombre(),
                        contexto.dependencia().geograficaId(),
                        contexto.dependencia().sedeNombre()));
    }

    private List<PedidoCompraItem> toDomainItems(List<PedidoCompraItemRequest> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new PedidoCompraItem(null, null, item.orden(), item.cantidad(), item.unidad(),
                        item.descripcion(), item.especificaciones(), item.referenciaWeb()))
                .toList();
    }

    private List<PedidoCompraItemResponse> toResponseItems(List<PedidoCompraItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> new PedidoCompraItemResponse(item.compraPedidoItemId(), item.compraPedidoId(),
                        item.orden(), item.cantidad(), item.unidad(), item.descripcion(),
                        item.especificaciones(), item.referenciaWeb()))
                .toList();
    }
}
