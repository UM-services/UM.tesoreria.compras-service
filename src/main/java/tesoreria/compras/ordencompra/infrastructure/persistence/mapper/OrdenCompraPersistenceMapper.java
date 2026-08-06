package tesoreria.compras.ordencompra.infrastructure.persistence.mapper;
import org.springframework.stereotype.Component; import tesoreria.compras.ordencompra.domain.model.*; import tesoreria.compras.ordencompra.infrastructure.persistence.entity.*;
@Component public class OrdenCompraPersistenceMapper {
 public OrdenCompraEntity toEntity(OrdenCompra o){var e=new OrdenCompraEntity(o.id(),o.numero(),o.fechaEmision(),o.proveedorId(),o.sedeId(),o.observaciones(),o.estado().name(),o.total());o.items().forEach(i->e.addItem(new OrdenCompraItemEntity(i.articuloId(),i.descripcion(),i.cantidad(),i.precioUnitario(),i.imputacionId())));return e;}
 public OrdenCompra toDomain(OrdenCompraEntity e){return new OrdenCompra(e.getId(),e.getNumero(),e.getFechaEmision(),e.getProveedorId(),e.getSedeId(),e.getObservaciones(),e.getItems().stream().map(i->new OrdenCompraItem(i.getArticuloId(),i.getDescripcion(),i.getCantidad(),i.getPrecioUnitario(),i.getImputacionId())).toList(),OrdenCompraEstado.valueOf(e.getEstado()));}
}
