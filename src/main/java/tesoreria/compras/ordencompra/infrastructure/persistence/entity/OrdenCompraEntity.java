package tesoreria.compras.ordencompra.infrastructure.persistence.entity;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.*;
@Entity @Table(name="compra_orden", uniqueConstraints=@UniqueConstraint(name="uk_compra_orden_numero",columnNames="numero")) @Getter @NoArgsConstructor(access=AccessLevel.PROTECTED)
public class OrdenCompraEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,length=16) private String numero; @Column(name="fecha_emision",nullable=false) private LocalDate fechaEmision; @Column(name="proveedor_id",nullable=false) private Integer proveedorId; @Column(name="sede_id",nullable=false) private Integer sedeId; @Column(length=1000) private String observaciones; @Column(nullable=false,length=32) private String estado; @Column(nullable=false,precision=19,scale=2) private BigDecimal total;
 @OneToMany(mappedBy="ordenCompra",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrdenCompraItemEntity> items=new ArrayList<>();
 public OrdenCompraEntity(Long id,String numero,LocalDate fecha,Integer proveedor,Integer sede,String observaciones,String estado,BigDecimal total){this.id=id;this.numero=numero;this.fechaEmision=fecha;this.proveedorId=proveedor;this.sedeId=sede;this.observaciones=observaciones;this.estado=estado;this.total=total;}
 public void addItem(OrdenCompraItemEntity item){items.add(item);item.setOrdenCompra(this);}
}
