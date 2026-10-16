package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItemSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendienteItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionItemResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionPendienteItemResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionResponse;

import java.util.List;

@Component
public class RecepcionDtoMapper {

    public RecepcionSolicitud toDomain(RecepcionRequest request) {
        if (request == null) return null;
        return new RecepcionSolicitud(request.compraFormalizacionId(), request.fecha(), request.remito(),
                request.patrimonio(), request.observacion(),
                request.items() == null ? List.of() : request.items().stream().map(this::toDomain).toList());
    }

    public RecepcionItemSolicitud toDomain(RecepcionItemRequest request) {
        if (request == null) return null;
        return new RecepcionItemSolicitud(request.compraFormalizacionItemId(), request.orden(),
                request.cantidadRecibida(), request.resultado(), request.observacion());
    }

    public RecepcionResponse toResponse(Recepcion domain) {
        if (domain == null) return null;
        return new RecepcionResponse(domain.compraRecepcionId(), domain.compraFormalizacionId(), domain.numero(),
                domain.ejercicioId(), domain.fecha(), domain.remito(), domain.resultado(), domain.patrimonio(),
                domain.observacion(), domain.usuarioId(),
                domain.items() == null ? List.of() : domain.items().stream().map(this::toResponse).toList(),
                domain.created(), domain.updated());
    }

    public RecepcionItemResponse toResponse(RecepcionItem domain) {
        if (domain == null) return null;
        return new RecepcionItemResponse(domain.compraRecepcionItemId(), domain.compraFormalizacionItemId(),
                domain.orden(), domain.cantidadRecibida(), domain.resultado(), domain.observacion());
    }

    public RecepcionPendienteResponse toResponse(RecepcionPendiente domain) {
        if (domain == null) return null;
        return new RecepcionPendienteResponse(domain.compraFormalizacionId(), domain.numero(), domain.numeroExterno(),
                domain.ejercicioId(), domain.proveedorId(), domain.fecha(), domain.monto(), domain.moneda(),
                domain.items() == null ? List.of() : domain.items().stream().map(this::toResponse).toList(),
                domain.recepciones() == null ? List.of()
                        : domain.recepciones().stream().map(this::toResponse).toList(),
                domain.completo());
    }

    public RecepcionPendienteItemResponse toResponse(RecepcionPendienteItem domain) {
        if (domain == null) return null;
        return new RecepcionPendienteItemResponse(domain.compraFormalizacionItemId(), domain.compraPedidoItemId(),
                domain.orden(), domain.cantidad(), domain.recibido(), domain.saldo(), domain.unidad(),
                domain.descripcion(), domain.especificaciones(), domain.precioUnitario(), domain.subtotal(),
                domain.marca(), domain.completo());
    }
}
