package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.ContextoInicioPedido;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraItem;
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
                toDomainItems(request.items()));
    }

    public PedidoCompraResponse toResponse(PedidoCompra domain) {
        if (domain == null) {
            return null;
        }
        return new PedidoCompraResponse(
                domain.compraPedidoId(), domain.numero(), domain.ejercicioId(), domain.fecha(), domain.estado(),
                domain.autorizanteId(), domain.solicitanteId(), domain.dependenciaId(), domain.facultadId(),
                domain.geograficaId(), domain.necesidad(), domain.fechaRequerida(), domain.urgente(),
                domain.urgenciaMotivo(), domain.montoConocido(), domain.montoEstimado(),
                domain.fuenteEstimacion(), toResponseItems(domain.items()));
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
