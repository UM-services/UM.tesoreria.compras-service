package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionAdjudicacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProceso;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoProveedor;
import tesoreria.compras.slice.pedidoCompra.domain.model.LimiteAutorizacion;
import tesoreria.compras.slice.pedidoCompra.domain.exception.LimiteAutorizacionExcedidoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoConflictoException;
import tesoreria.compras.slice.pedidoCompra.domain.exception.CotizacionProcesoInvalidaException;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.AutoridadGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.CotizacionProcesoGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotizacionProcesoServiceTest {

    @Mock private CotizacionProcesoGateway cotizacionProcesoGateway;
    @Mock private EjercicioActualGateway ejercicioActualGateway;
    @Mock private AutoridadGateway autoridadGateway;

    @InjectMocks
    private CotizacionProcesoService service;

    private CotizacionProceso proceso() {
        return new CotizacionProceso(1L, null, 15, null, "BORRADOR", null, null, null, 2, null,
                null, null, null, null, 38, List.of(), List.of());
    }

    @Test
    void delegaciones() {
        when(cotizacionProcesoGateway.pendientes(eq(15), any())).thenReturn(List.of(
                new CotizacionPendiente(100, 2, "PC-1", 14, 1, null, "Unidad", "Notebook", null, null)));
        assertThat(service.pendientes(15, List.of(14))).hasSize(1);

        when(cotizacionProcesoGateway.listar(any())).thenReturn(List.of(proceso()));
        assertThat(service.listar(new CotizacionProcesoFiltro(null, null, null, null))).hasSize(1);

        when(cotizacionProcesoGateway.getById(1L)).thenReturn(proceso());
        assertThat(service.getById(1L).compraCotizacionId()).isEqualTo(1L);

        when(cotizacionProcesoGateway.crear(any())).thenReturn(proceso());
        assertThat(service.crear(proceso())).isEqualTo(proceso());

        when(cotizacionProcesoGateway.actualizar(eq(1L), any())).thenReturn(proceso());
        assertThat(service.actualizar(1L, proceso())).isEqualTo(proceso());

        when(cotizacionProcesoGateway.enviar(1L)).thenReturn(proceso());
        assertThat(service.enviar(1L)).isEqualTo(proceso());

        when(cotizacionProcesoGateway.anular(1L, "x")).thenReturn(proceso());
        assertThat(service.anular(1L, "x")).isEqualTo(proceso());
    }

    @Test
    void pendientesSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(cotizacionProcesoGateway.pendientes(eq(15), any())).thenReturn(List.of());

        assertThat(service.pendientes(null, null)).isEmpty();
    }

    @Test
    void respuestasPendientesDelegaYResuelveEjercicio() {
        when(cotizacionProcesoGateway.respuestasPendientes(15)).thenReturn(List.of());
        assertThat(service.respuestasPendientes(15)).isEmpty();

        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        assertThat(service.respuestasPendientes(null)).isEmpty();
    }

    @Test
    void crearSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(cotizacionProcesoGateway.crear(any())).thenReturn(proceso());

        service.crear(new CotizacionProceso(null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, 38, List.of(), List.of()));

        verify(cotizacionProcesoGateway).crear(eq(new CotizacionProceso(null, null, 15, null, null, null,
                null, null, null, null, null, null, null, null, 38, List.of(), List.of())));
    }

    @Test
    void etapa6CDelegaEnElGateway() {
        when(cotizacionProcesoGateway.registrarRespuesta(eq(1L), eq(5L), any())).thenReturn(null);
        assertThat(service.registrarRespuesta(1L, 5L, null)).isNull();

        when(cotizacionProcesoGateway.marcarNoCotiza(1L, 5L, "x")).thenReturn(null);
        assertThat(service.marcarNoCotiza(1L, 5L, "x")).isNull();

        when(cotizacionProcesoGateway.comparacion(1L)).thenReturn(proceso());
        assertThat(service.comparacion(1L)).isEqualTo(proceso());

        when(cotizacionProcesoGateway.recomendar(1L, 5L, "m")).thenReturn(proceso());
        assertThat(service.recomendar(1L, 5L, "m")).isEqualTo(proceso());

        when(cotizacionProcesoGateway.cerrar(1L)).thenReturn(proceso());
        assertThat(service.cerrar(1L)).isEqualTo(proceso());
    }

    private CotizacionProceso recomendado() {
        return new CotizacionProceso(1L, "SC-1", 15, null, "RECOMENDADA", null, null, null, 2, null,
                null, 5L, null, null, 38,
                List.of(),
                List.of(new CotizacionProcesoProveedor(5L, 7, "p@x.com", "RESPONDIDA", null, null, null,
                        null, new BigDecimal("28000.00"), null, null, null, null, List.of())));
    }

    @Test
    void adjudicarValidaElLimiteYAplica() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());
        when(autoridadGateway.getLimite(38L, 15)).thenReturn(new LimiteAutorizacion(38L, 15, 3,
                new BigDecimal("100000.00"), new BigDecimal("300000.00"), false, true));
        when(cotizacionProcesoGateway.adjudicar(eq(1L), eq("ADJUDICADA"), eq(5L), eq(7),
                eq(new BigDecimal("28000.00")), any(), eq(38L), eq(new BigDecimal("300000.00"))))
                .thenReturn(new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7, new BigDecimal("28000.00"),
                        "ADJUDICADA", "menor", 38L, new BigDecimal("300000.00"), null));

        CotizacionAdjudicacion resultado = service.adjudicar(38L, 1L, null, "menor");

        assertThat(resultado.decision()).isEqualTo("ADJUDICADA");
        assertThat(resultado.proveedorAdjudicadoId()).isEqualTo(7);
    }

    @Test
    void adjudicarExcedeElLimiteFalla() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());
        when(autoridadGateway.getLimite(38L, 15)).thenReturn(new LimiteAutorizacion(38L, 15, 1,
                new BigDecimal("100000.00"), new BigDecimal("10000.00"), false, true));

        assertThatThrownBy(() -> service.adjudicar(38L, 1L, null, "menor"))
                .isInstanceOf(LimiteAutorizacionExcedidoException.class);
    }

    @Test
    void devolverNoValidaElLimite() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());
        when(cotizacionProcesoGateway.adjudicar(eq(1L), eq("DEVUELTA_A_COMPRAS"), eq(5L), eq(7),
                any(), any(), eq(38L), any())).thenReturn(new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7,
                        new BigDecimal("28000.00"), "DEVUELTA_A_COMPRAS", "revisar", 38L, null, null));

        assertThat(service.devolver(38L, 1L, "revisar").decision()).isEqualTo("DEVUELTA_A_COMPRAS");
    }

    @Test
    void adjudicacionBandejaListaRecomendados() {
        when(cotizacionProcesoGateway.listar(any())).thenReturn(List.of(recomendado()));
        assertThat(service.adjudicacionBandeja(38L)).hasSize(1);
    }

    @Test
    void adjudicarIlimitadoNoValidaMonto() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());
        when(autoridadGateway.getLimite(38L, 15)).thenReturn(new LimiteAutorizacion(38L, 15, null, null,
                null, true, true));
        when(cotizacionProcesoGateway.adjudicar(eq(1L), eq("ADJUDICADA"), eq(5L), eq(7), any(), any(), eq(38L),
                isNull())).thenReturn(new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7, new BigDecimal("28000.00"),
                        "ADJUDICADA", "ok", 38L, null, null));

        assertThat(service.adjudicar(38L, 1L, null, "ok").decision()).isEqualTo("ADJUDICADA");
    }

    @Test
    void adjudicarOfertaInexistenteFalla() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());

        assertThatThrownBy(() -> service.adjudicar(38L, 1L, 999L, "x"))
                .isInstanceOf(CotizacionProcesoInvalidaException.class);
    }

    @Test
    void procesoNoRecomendadoFalla() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(proceso());

        assertThatThrownBy(() -> service.devolver(38L, 1L, "x"))
                .isInstanceOf(CotizacionProcesoConflictoException.class);
    }

    @Test
    void noAdjudicarDelega() {
        when(cotizacionProcesoGateway.getById(1L)).thenReturn(recomendado());
        when(cotizacionProcesoGateway.adjudicar(eq(1L), eq("NO_ADJUDICADA"), eq(5L), eq(7), any(), any(), eq(38L),
                any())).thenReturn(new CotizacionAdjudicacion(9L, 1L, 15, 5L, 7, new BigDecimal("28000.00"),
                        "NO_ADJUDICADA", "sin fondos", 38L, null, null));

        assertThat(service.noAdjudicar(38L, 1L, "sin fondos").decision()).isEqualTo("NO_ADJUDICADA");
    }

    @Test
    void adjudicacionLimiteSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(autoridadGateway.getLimite(38L, 15)).thenReturn(new LimiteAutorizacion(38L, 15, null, null,
                null, true, true));

        assertThat(service.adjudicacionLimite(38L, null).ilimitado()).isTrue();
    }
}
