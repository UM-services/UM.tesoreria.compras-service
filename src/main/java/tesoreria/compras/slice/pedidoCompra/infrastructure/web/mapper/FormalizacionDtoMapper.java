package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionItemResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionResponse;

import java.util.List;

@Component
public class FormalizacionDtoMapper {

    public FormalizacionSolicitud toDomain(FormalizacionRequest request) {
        if (request == null) return null;
        return new FormalizacionSolicitud(request.compraCotizacionId(), request.numeroExterno(), request.moneda(),
                request.monto(), request.condicionPago(), request.plazoEntrega(), request.garantia(), request.objeto(),
                request.observacion());
    }

    public FormalizacionResponse toResponse(Formalizacion domain) {
        if (domain == null) return null;
        return new FormalizacionResponse(domain.compraFormalizacionId(), domain.compraAdjudicacionId(), domain.numero(),
                domain.numeroExterno(), domain.ejercicioId(), domain.fecha(), domain.proveedorId(), domain.moneda(),
                domain.monto(), domain.condicionPago(), domain.plazoEntrega(), domain.garantia(), domain.objeto(),
                domain.observacion(), domain.usuarioId(),
                domain.items() == null ? List.of() : domain.items().stream().map(this::toResponse).toList(),
                domain.created(), domain.updated());
    }

    public FormalizacionItemResponse toResponse(FormalizacionItem domain) {
        if (domain == null) return null;
        return new FormalizacionItemResponse(domain.compraFormalizacionItemId(), domain.compraCotizacionItemId(),
                domain.compraPedidoItemId(), domain.orden(), domain.cantidad(), domain.unidad(), domain.descripcion(),
                domain.especificaciones(), domain.precioUnitario(), domain.subtotal(), domain.marca());
    }

    public FormalizacionPendienteResponse toResponse(FormalizacionPendiente domain) {
        if (domain == null) return null;
        return new FormalizacionPendienteResponse(domain.compraCotizacionId(), domain.numero(), domain.ejercicioId(),
                domain.compraAdjudicacionId(), domain.compraCotizacionProveedorId(), domain.proveedorId(), domain.monto(),
                domain.moneda(), domain.condicionPago(), domain.plazoEntrega(), domain.garantia(),
                domain.items() == null ? List.of() : domain.items().stream().map(this::toResponse).toList());
    }
}
