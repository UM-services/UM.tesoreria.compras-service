package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FormalizacionDtoMapperTest {

    private final FormalizacionDtoMapper mapper = new FormalizacionDtoMapper();

    @Test
    void toDomainMapeaLaSolicitud() {
        var solicitud = mapper.toDomain(new FormalizacionRequest(1L, "OC-EXT", "ARS",
                new BigDecimal("28000.00"), "30 días", "48 horas", "12 meses", "<p>Objeto</p>", "obs"));

        assertThat(solicitud.compraCotizacionId()).isEqualTo(1L);
        assertThat(solicitud.numeroExterno()).isEqualTo("OC-EXT");
        assertThat(solicitud.monto()).isEqualByComparingTo("28000.00");
        assertThat(solicitud.objeto()).isEqualTo("<p>Objeto</p>");
    }

    @Test
    void toResponseDeLaOcIncluyeItems() {
        FormalizacionResponse response = mapper.toResponse(new Formalizacion(1L, 9L, "OC-2026-000001", null, 15,
                null, 7, "ARS", new BigDecimal("28000.00"), null, null, null, null, null, 38,
                List.of(new FormalizacionItem(2L, 9L, 100, 1, new BigDecimal("3.00"), "Unidad", "Notebook", null,
                        new BigDecimal("9000.00"), new BigDecimal("27000.00"), "ACME")),
                null, null));

        assertThat(response.numero()).isEqualTo("OC-2026-000001");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).descripcion()).isEqualTo("Notebook");
    }

    @Test
    void toResponseDelPendiente() {
        FormalizacionPendienteResponse response = mapper.toResponse(new FormalizacionPendiente(1L, "SC-1", 15,
                9L, 5L, 7, new BigDecimal("28000.00"), "ARS", "30 días", "48 horas", "12 meses",
                List.of(new FormalizacionItem(null, 9L, 100, 1, new BigDecimal("3.00"), null, "Notebook", null,
                        null, null, null))));

        assertThat(response.compraCotizacionId()).isEqualTo(1L);
        assertThat(response.proveedorId()).isEqualTo(7);
        assertThat(response.items()).hasSize(1);
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toResponse((Formalizacion) null)).isNull();
        assertThat(mapper.toResponse((FormalizacionItem) null)).isNull();
        assertThat(mapper.toResponse((FormalizacionPendiente) null)).isNull();
    }
}
