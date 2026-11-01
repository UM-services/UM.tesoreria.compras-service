package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedorPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRespuestaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CotizacionProcesoDtoMapperTest {

    private final CotizacionProcesoDtoMapper mapper = new CotizacionProcesoDtoMapper();

    @Test
    void toDomainMapeaItemsYProveedores() {
        CotizacionProcesoRequest request = new CotizacionProcesoRequest(15, null, "obs", null,
                new BigDecimal("100.00"), 2, null, 38,
                List.of(new CotizacionProcesoItemRequest(100, 1, new BigDecimal("3.00"))),
                List.of(new CotizacionProcesoProveedorRequest(7, "p@x.com", null)));

        CotizacionProceso proceso = mapper.toDomain(request);

        assertThat(proceso.ejercicioId()).isEqualTo(15);
        assertThat(proceso.cantidadMinima()).isEqualTo(2);
        assertThat(proceso.items()).hasSize(1);
        assertThat(proceso.items().get(0).compraPedidoItemId()).isEqualTo(100);
        assertThat(proceso.proveedores()).hasSize(1);
        assertThat(proceso.proveedores().get(0).proveedorId()).isEqualTo(7);
    }

    @Test
    void toDomainConNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        CotizacionProceso proceso = mapper.toDomain(new CotizacionProcesoRequest(
                15, null, null, null, null, null, null, null, null, null));
        assertThat(proceso.items()).isEmpty();
        assertThat(proceso.proveedores()).isEmpty();
    }

    @Test
    void toResponseMapeaElAgregado() {
        CotizacionProceso proceso = new CotizacionProceso(1L, "SC-1", 15, null, "ENVIADA", null, null,
                null, 2, null, null, 5L, "f", null, 38,
                List.of(new CotizacionProcesoItem(9L, 100, 1, new BigDecimal("3.00"))),
                List.of(new CotizacionProcesoProveedor(5L, 7, "p@x.com", "ENVIADA", null, null, null,
                        null, null, null, null, null, null, List.of())));

        CotizacionProcesoResponse response = mapper.toResponse(proceso);

        assertThat(response.estado()).isEqualTo("ENVIADA");
        assertThat(response.items()).hasSize(1);
        assertThat(response.proveedores()).hasSize(1);
        assertThat(response.proveedores().get(0).estado()).isEqualTo("ENVIADA");
    }

    @Test
    void toResponseNulos() {
        assertThat(mapper.toResponse((CotizacionProceso) null)).isNull();
        assertThat(mapper.toResponse((CotizacionPendiente) null)).isNull();
        assertThat(mapper.toResponse(new CotizacionProceso(1L, null, 15, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null)).items()).isEmpty();
    }

    @Test
    void toResponseProveedorPendienteMapea() {
        CotizacionProcesoProveedorPendiente pendiente = new CotizacionProcesoProveedorPendiente(1L, "SC-1", 15, null,
                new CotizacionProcesoProveedor(5L, 7, "p@x.com", "ENVIADA", null, null, null, null, null,
                        null, null, null, null, List.of()),
                List.of(new CotizacionProcesoItem(9L, 100, 1, new BigDecimal("3.00"))));

        var response = mapper.toResponse(pendiente);

        assertThat(response.numero()).isEqualTo("SC-1");
        assertThat(response.proveedor().proveedorId()).isEqualTo(7);
        assertThat(response.items()).hasSize(1);
        assertThat(mapper.toResponse((CotizacionProcesoProveedorPendiente) null)).isNull();

        assertThat(pendiente.compraCotizacionId()).isEqualTo(1L);
        assertThat(pendiente.ejercicioId()).isEqualTo(15);
        assertThat(pendiente.fechaLimiteRespuesta()).isNull();
        assertThat(pendiente).isEqualTo(new CotizacionProcesoProveedorPendiente(1L, "SC-1", 15, null,
                pendiente.proveedor(), pendiente.items()));
        assertThat(pendiente.toString()).isNotBlank();
        assertThat(pendiente.hashCode()).isEqualTo(pendiente.hashCode());
    }

    @Test
    void toResponseAdjudicacionMapea() {
        CotizacionAdjudicacion adjudicacion = new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7,
                new BigDecimal("28000.00"), "ADJUDICADA", "menor", 38L, new BigDecimal("300000.00"), null);

        var response = mapper.toResponse(adjudicacion);

        assertThat(response.decision()).isEqualTo("ADJUDICADA");
        assertThat(response.proveedorAdjudicadoId()).isEqualTo(7);
        assertThat(response.monto()).isEqualByComparingTo("28000.00");
        assertThat(mapper.toResponse((CotizacionAdjudicacion) null)).isNull();
    }

    @Test
    void toDomainRespuestaMapea() {
        CotizacionProcesoRespuestaRequest request = new CotizacionProcesoRespuestaRequest(null, "ARS",
                new BigDecimal("100.00"), null, null, null, null,
                List.of(new CotizacionProcesoProveedorItemRequest(1L, true, new BigDecimal("10.00"),
                        new BigDecimal("50.00"), "Marca", null)));

        CotizacionProcesoProveedor proveedor = mapper.toDomainRespuesta(request);

        assertThat(proveedor.moneda()).isEqualTo("ARS");
        assertThat(proveedor.items()).hasSize(1);
        assertThat(proveedor.items().get(0).cotiza()).isTrue();
        assertThat(mapper.toDomainRespuesta(null)).isNull();
    }
}
