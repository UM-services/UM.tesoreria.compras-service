package tesoreria.compras.ordencompra.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra_orden",
        uniqueConstraints = @UniqueConstraint(name = "uk_compra_orden_numero", columnNames = "numero"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdenCompraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String numero;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "proveedor_id", nullable = false)
    private Integer proveedorId;

    @Column(name = "sede_id", nullable = false)
    private Integer sedeId;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false, length = 32)
    private String estado;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenCompraItemEntity> items = new ArrayList<>();

    public OrdenCompraEntity(Long id, String numero, LocalDate fechaEmision, Integer proveedorId, Integer sedeId,
                             String observaciones, String estado, BigDecimal total) {
        this.id = id;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.proveedorId = proveedorId;
        this.sedeId = sedeId;
        this.observaciones = observaciones;
        this.estado = estado;
        this.total = total;
    }

    public void addItem(OrdenCompraItemEntity item) {
        items.add(item);
        item.setOrdenCompra(this);
    }
}
