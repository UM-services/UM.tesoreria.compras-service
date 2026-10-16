package tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionItem;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendienteItem;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecepcionDtoMapperTest {

    private final RecepcionDtoMapper mapper = new RecepcionDtoMapper();

    @Test
    void toDomainMapeaLaSolicitudYElDetalle() {
        var solicitud = mapper.toDomain(new RecepcionRequest(50L, null, "R-1", "INTERVENIDO", "obs",
                List.of(new RecepcionItemRequest(100L, 1, new BigDecimal("2.00"), "CONFORME", "ok"))));

        assertThat(solicitud.compraFormalizacionId()).isEqualTo(50L);
        assertThat(solicitud.remito()).isEqualTo("R-1");
        assertThat(solicitud.items()).hasSize(1);
        assertThat(solicitud.items().get(0).cantidadRecibida()).isEqualByComparingTo("2.00");
        assertThat(solicitud.items().get(0).resultado()).isEqualTo("CONFORME");
    }

    @Test
    void toDomainSinItemsDevuelveListaVacia() {
        assertThat(mapper.toDomain(new RecepcionRequest(50L, null, null, null, null, null)).items()).isEmpty();
    }

    @Test
    void toResponseMapeaLaRecepcionConDetalle() {
        Recepcion domain = new Recepcion(1L, 50L, "RC-2026-000001", 15, null, "R-1", "CON_OBSERVACIONES",
                "INTERVENIDO", "obs", 38, List.of(new RecepcionItem(2L, 100L, 1, new BigDecimal("1.00"),
                "CON_OBSERVACIONES", "detalle")), null, null);

        var response = mapper.toResponse(domain);

        assertThat(response.numero()).isEqualTo("RC-2026-000001");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).resultado()).isEqualTo("CON_OBSERVACIONES");
    }

    @Test
    void toResponseMapeaLaPendienteConSaldos() {
        RecepcionPendiente domain = new RecepcionPendiente(50L, "OC-2026-000003", "OC-EXT", 15, 7, null,
                new BigDecimal("28000.00"), "ARS",
                List.of(new RecepcionPendienteItem(100L, 1000, 1, new BigDecimal("3.00"),
                        new BigDecimal("1.00"), new BigDecimal("2.00"), "Unidad", "Notebook", null,
                        new BigDecimal("9000.00"), new BigDecimal("27000.00"), "ACME", false)),
                List.of(new Recepcion(1L, 50L, "RC-2026-000001", 15, null, null, null, null, null, null,
                        null, null, null)),
                false);

        var response = mapper.toResponse(domain);

        assertThat(response.numero()).isEqualTo("OC-2026-000003");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).saldo()).isEqualByComparingTo("2.00");
        assertThat(response.recepciones()).hasSize(1);
    }

    @Test
    void toResponseConNulosNoRompe() {
        Recepcion sinItems = new Recepcion(1L, 50L, "RC-1", 15, null, null, null, null, null, null,
                null, null, null);
        RecepcionPendiente pendienteSinNada = new RecepcionPendiente(50L, "OC-1", null, 15, 7, null, null,
                null, null, null, null);

        assertThat(mapper.toResponse(sinItems).items()).isEmpty();
        assertThat(mapper.toResponse(pendienteSinNada).items()).isEmpty();
        assertThat(mapper.toResponse(pendienteSinNada).recepciones()).isEmpty();
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toDomain((RecepcionRequest) null)).isNull();
        assertThat(mapper.toDomain((RecepcionItemRequest) null)).isNull();
        assertThat(mapper.toResponse((Recepcion) null)).isNull();
        assertThat(mapper.toResponse((RecepcionItem) null)).isNull();
        assertThat(mapper.toResponse((RecepcionPendiente) null)).isNull();
        assertThat(mapper.toResponse((RecepcionPendienteItem) null)).isNull();
    }
}
