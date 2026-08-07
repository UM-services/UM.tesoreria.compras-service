package tesoreria.compras.ordencompra.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.domain.model.PaginaOrdenCompra;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraItemRequest;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraItemResponse;
import tesoreria.compras.ordencompra.infrastructure.web.dto.OrdenCompraResponse;
import tesoreria.compras.ordencompra.infrastructure.web.dto.PaginaOrdenCompraResponse;

import java.util.List;

@Component
public class OrdenCompraDtoMapper {

    public List<OrdenCompraItem> toItems(List<OrdenCompraItemRequest> items) {
        return items.stream()
                .map(item -> OrdenCompraItem.nuevo(item.articuloId(), item.descripcion(), item.cantidad(),
                        item.precioUnitario(), item.imputacionId()))
                .toList();
    }

    public OrdenCompraResponse toResponse(OrdenCompra orden) {
        return new OrdenCompraResponse(orden.id(), orden.numero(), orden.fechaEmision(), orden.proveedorId(),
                orden.sedeId(), orden.observaciones(), orden.estado().name(), orden.total(),
                orden.items().stream().map(this::toItemResponse).toList());
    }

    public PaginaOrdenCompraResponse toResponse(PaginaOrdenCompra pagina) {
        return new PaginaOrdenCompraResponse(pagina.contenido().stream().map(this::toResponse).toList(),
                pagina.pagina(), pagina.tamano(), pagina.totalElementos(), pagina.totalPaginas());
    }

    private OrdenCompraItemResponse toItemResponse(OrdenCompraItem item) {
        return new OrdenCompraItemResponse(item.id(), item.articuloId(), item.descripcion(), item.cantidad(),
                item.precioUnitario(), item.imputacionId(), item.subtotal());
    }
}
