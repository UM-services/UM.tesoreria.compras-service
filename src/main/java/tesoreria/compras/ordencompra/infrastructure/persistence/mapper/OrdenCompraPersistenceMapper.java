package tesoreria.compras.ordencompra.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInconsistenteException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompra;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraItem;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraEntity;
import tesoreria.compras.ordencompra.infrastructure.persistence.entity.OrdenCompraItemEntity;

@Component
public class OrdenCompraPersistenceMapper {

    public OrdenCompraEntity toEntity(OrdenCompra orden) {
        OrdenCompraEntity entity = new OrdenCompraEntity(orden.id(), orden.numero(), orden.fechaEmision(),
                orden.proveedorId(), orden.sedeId(), orden.observaciones(), orden.estado().name(), orden.total());
        orden.items().forEach(item -> entity.addItem(toItemEntity(item)));
        return entity;
    }

    /**
     * El total viaja a la base para que reportes y BI no tengan que sumar ítems, pero la
     * fuente de verdad son los ítems. Si divergen es corrupción y hay que verla, no taparla.
     */
    public OrdenCompra toDomain(OrdenCompraEntity entity) {
        OrdenCompra orden = new OrdenCompra(entity.getId(), entity.getNumero(), entity.getFechaEmision(),
                entity.getProveedorId(), entity.getSedeId(), entity.getObservaciones(),
                entity.getItems().stream().map(this::toItemDomain).toList(),
                OrdenCompraEstado.valueOf(entity.getEstado()));
        if (entity.getTotal() != null && entity.getTotal().compareTo(orden.total()) != 0) {
            throw new OrdenCompraInconsistenteException(entity.getNumero(), entity.getTotal(), orden.total());
        }
        return orden;
    }

    private OrdenCompraItemEntity toItemEntity(OrdenCompraItem item) {
        return new OrdenCompraItemEntity(item.id(), item.articuloId(), item.descripcion(), item.cantidad(),
                item.precioUnitario(), item.imputacionId());
    }

    private OrdenCompraItem toItemDomain(OrdenCompraItemEntity item) {
        return new OrdenCompraItem(item.getId(), item.getArticuloId(), item.getDescripcion(), item.getCantidad(),
                item.getPrecioUnitario(), item.getImputacionId());
    }
}
