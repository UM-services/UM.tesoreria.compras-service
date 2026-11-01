package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionProcesoService;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.AdjudicarCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FundamentoCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionProcesoDtoMapper;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdjudicacionCotizacionControllerTest {

    @Mock
    private CotizacionProcesoService service;

    private AdjudicacionCotizacionController controller;

    @BeforeEach
    void setUp() {
        controller = new AdjudicacionCotizacionController(service, new CotizacionProcesoDtoMapper(),
                new PedidoCompraDtoMapper());
    }

    private CotizacionProceso recomendado() {
        return new CotizacionProceso(1L, "SC-1", 15, null, "RECOMENDADA", null, null, null, 2, null,
                null, 5L, null, null, 38, List.of(),
                List.of(new CotizacionProcesoProveedor(5L, 7, "p@x.com", "RESPONDIDA", null, null, null,
                        null, new BigDecimal("28000.00"), null, null, null, null, List.of())));
    }

    private CotizacionAdjudicacion adjudicacion() {
        return new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7, new BigDecimal("28000.00"), "ADJUDICADA",
                "menor", 38L, new BigDecimal("300000.00"), null);
    }

    @Test
    void bandejaDevuelveLaLista() {
        when(service.adjudicacionBandeja(38L)).thenReturn(List.of(recomendado()));

        assertThat(controller.bandeja(38L).getBody()).hasSize(1);
    }

    @Test
    void limiteDevuelve() {
        LimiteAutorizacion limite = new LimiteAutorizacion(38L, 15, 3, new BigDecimal("100000.00"),
                new BigDecimal("300000.00"), false, true);
        when(service.adjudicacionLimite(38L, null)).thenReturn(limite);
        when(service.adjudicacionLimite(38L, 15)).thenReturn(limite);

        assertThat(controller.limiteActual(38L).getBody().limite()).isEqualByComparingTo("300000.00");
        assertThat(controller.limite(38L, 15).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void adjudicarDelegaConYOfertaElegida() {
        when(service.adjudicar(38L, 1L, 5L, "menor")).thenReturn(adjudicacion());
        when(service.adjudicar(38L, 1L, null, null)).thenReturn(adjudicacion());

        assertThat(controller.adjudicar(38L, 1L, new AdjudicarCotizacionRequest(5L, "menor")).getBody().decision())
                .isEqualTo("ADJUDICADA");
        assertThat(controller.adjudicar(38L, 1L, null).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void devolverYNoAdjudicarDelegan() {
        when(service.devolver(38L, 1L, "revisar")).thenReturn(adjudicacion());
        when(service.noAdjudicar(38L, 1L, "sin fondos")).thenReturn(adjudicacion());

        assertThat(controller.devolver(38L, 1L, new FundamentoCotizacionRequest("revisar"))
                .getStatusCode().value()).isEqualTo(200);
        assertThat(controller.noAdjudicar(38L, 1L, new FundamentoCotizacionRequest("sin fondos"))
                .getStatusCode().value()).isEqualTo(200);
        assertThat(controller.devolver(38L, 1L, null).getStatusCode().value()).isEqualTo(200);
    }

    @Test
    void adjudicacionDelega() {
        when(service.adjudicacion(1L)).thenReturn(Optional.of(adjudicacion()));

        assertThat(controller.adjudicacion(1L).getBody().compraAdjudicacionId()).isEqualTo(9L);
    }
}
