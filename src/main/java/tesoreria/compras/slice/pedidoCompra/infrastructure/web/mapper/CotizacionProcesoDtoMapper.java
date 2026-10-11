package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionAdjudicacionResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoItemResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorItemResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRespuestaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoResponse;

import java.util.List;

@Component
public class CotizacionProcesoDtoMapper {

    public CotizacionProceso toDomain(CotizacionProcesoRequest request) {
        if (request == null) return null;
        List<CotizacionProcesoItem> items = request.items() == null ? List.of()
                : request.items().stream()
                        .map(i -> new CotizacionProcesoItem(null, i.compraPedidoItemId(), i.orden(), i.cantidad()))
                        .toList();
        List<CotizacionProcesoProveedor> proveedores = request.proveedores() == null ? List.of()
                : request.proveedores().stream()
                        .map(p -> new CotizacionProcesoProveedor(null, p.proveedorId(), p.email(), null, null,
                                p.observacion(), null, null, null, null, null, null, null, List.of()))
                        .toList();
        return new CotizacionProceso(null, null, request.ejercicioId(), request.fecha(), null,
                request.observacion(), request.fechaLimiteRespuesta(), request.montoEstimado(),
                request.cantidadMinima(), request.excepcionMotivo(), null, null, null, null, request.usuarioId(),
                items, proveedores);
    }

    /** Respuesta de un proveedor (cabecera + precios por ítem). */
    public CotizacionProcesoProveedor toDomainRespuesta(CotizacionProcesoRespuestaRequest request) {
        if (request == null) return null;
        List<CotizacionProcesoProveedorItem> items = request.items() == null ? List.of()
                : request.items().stream()
                        .map(i -> new CotizacionProcesoProveedorItem(null, i.compraCotizacionItemId(),
                                i.cotiza() == null ? Boolean.TRUE : i.cotiza(), i.precioUnitario(), i.subtotal(),
                                i.marca(), i.observacion()))
                        .toList();
        return new CotizacionProcesoProveedor(null, null, null, null, null, null, request.fechaRespuesta(),
                request.moneda(), request.total(), request.plazoEntrega(), request.condicionPago(),
                request.garantia(), request.validezOferta(), items);
    }

    public CotizacionProcesoResponse toResponse(CotizacionProceso domain) {
        if (domain == null) return null;
        List<CotizacionProcesoItemResponse> items = domain.items() == null ? List.of()
                : domain.items().stream()
                        .map(i -> new CotizacionProcesoItemResponse(i.compraCotizacionItemId(), i.compraPedidoItemId(),
                                i.orden(), i.cantidad()))
                        .toList();
        List<CotizacionProcesoProveedorResponse> proveedores = domain.proveedores() == null ? List.of()
                : domain.proveedores().stream().map(this::toResponse).toList();
        return new CotizacionProcesoResponse(domain.compraCotizacionId(), domain.numero(), domain.ejercicioId(),
                domain.fecha(), domain.estado(), domain.observacion(), domain.fechaLimiteRespuesta(),
                domain.montoEstimado(), domain.cantidadMinima(), domain.excepcionMotivo(), domain.fechaEnvio(),
                domain.proveedorRecomendadoId(), domain.fundamentoRecomendacion(), domain.fechaRecomendacion(),
                domain.usuarioId(), items, proveedores);
    }

    public CotizacionProcesoProveedorResponse toResponse(CotizacionProcesoProveedor proveedor) {
        if (proveedor == null) return null;
        List<CotizacionProcesoProveedorItemResponse> items = proveedor.items() == null ? List.of()
                : proveedor.items().stream()
                        .map(i -> new CotizacionProcesoProveedorItemResponse(i.compraCotizacionProveedorItemId(),
                                i.compraCotizacionItemId(), i.cotiza(), i.precioUnitario(), i.subtotal(),
                                i.marca(), i.observacion()))
                        .toList();
        return new CotizacionProcesoProveedorResponse(proveedor.compraCotizacionProveedorId(),
                proveedor.proveedorId(), proveedor.email(), proveedor.estado(), proveedor.fechaEnvio(),
                proveedor.observacion(), proveedor.fechaRespuesta(), proveedor.moneda(), proveedor.total(),
                proveedor.plazoEntrega(), proveedor.condicionPago(), proveedor.garantia(),
                proveedor.validezOferta(), items);
    }

    public CotizacionPendienteResponse toResponse(CotizacionPendiente pendiente) {
        if (pendiente == null) return null;
        return new CotizacionPendienteResponse(pendiente.compraPedidoItemId(), pendiente.compraPedidoId(),
                pendiente.pedidoNumero(), pendiente.dependenciaId(), pendiente.orden(), pendiente.cantidad(),
                pendiente.unidad(), pendiente.descripcion(), pendiente.especificaciones(), pendiente.referenciaWeb());
    }

    /** Documento de proveedor pendiente de respuesta (bandeja "Cotizaciones pedidas"). */
    public CotizacionProcesoProveedorPendienteResponse toResponse(CotizacionProcesoProveedorPendiente pendiente) {
        if (pendiente == null) return null;
        List<CotizacionProcesoItemResponse> items = pendiente.items() == null ? List.of()
                : pendiente.items().stream()
                        .map(i -> new CotizacionProcesoItemResponse(i.compraCotizacionItemId(), i.compraPedidoItemId(),
                                i.orden(), i.cantidad()))
                        .toList();
        return new CotizacionProcesoProveedorPendienteResponse(pendiente.compraCotizacionId(), pendiente.numero(),
                pendiente.ejercicioId(), pendiente.fechaLimiteRespuesta(), toResponse(pendiente.proveedor()), items);
    }

    public CotizacionAdjudicacionResponse toResponse(CotizacionAdjudicacion adjudicacion) {
        if (adjudicacion == null) return null;
        return new CotizacionAdjudicacionResponse(adjudicacion.compraAdjudicacionId(), adjudicacion.compraCotizacionId(),
                adjudicacion.ejercicioId(), adjudicacion.compraCotizacionProveedorId(),
                adjudicacion.proveedorAdjudicadoId(), adjudicacion.monto(), adjudicacion.decision(),
                adjudicacion.fundamento(), adjudicacion.autoridadUsuarioId(), adjudicacion.limiteAplicado(),
                adjudicacion.fecha());
    }
}
