package tesoreria.compras.ordencompra.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "compra_orden_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdenCompraItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private OrdenCompraEntity ordenCompra;

    @Column(name = "articulo_id", nullable = false)
    private Integer articuloId;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "imputacion_id", nullable = false)
    private Long imputacionId;

    /**
     * El id viaja de vuelta desde el dominio para que un merge actualice la fila existente
     * en lugar de borrarla e insertar una nueva. Es null sólo en ítems todavía no persistidos.
     */
    public OrdenCompraItemEntity(Long id, Integer articuloId, String descripcion, BigDecimal cantidad,
                                 BigDecimal precioUnitario, Long imputacionId) {
        this.id = id;
        this.articuloId = articuloId;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.imputacionId = imputacionId;
    }
}
