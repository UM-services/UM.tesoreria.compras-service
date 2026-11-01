package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.FormalizacionGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormalizacionServiceTest {

    @Mock private FormalizacionGateway formalizacionGateway;
    @Mock private EjercicioActualGateway ejercicioActualGateway;

    @InjectMocks
    private FormalizacionService service;

    private Formalizacion oc() {
        return new Formalizacion(1L, 9L, "OC-2026-000001", null, 15, null, 7, "ARS",
                new BigDecimal("28000.00"), null, null, null, null, null, 38, List.of(), null, null);
    }

    @Test
    void pendientesDelegaConElEjercicioIndicado() {
        when(formalizacionGateway.pendientes(15)).thenReturn(List.of());
        assertThat(service.pendientes(15)).isEmpty();
    }

    @Test
    void pendientesSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(formalizacionGateway.pendientes(15)).thenReturn(List.of(
                new FormalizacionPendiente(1L, "SC-1", 15, 9L, 5L, 7, new BigDecimal("28000.00"), "ARS",
                        null, null, null, List.of())));

        assertThat(service.pendientes(null)).hasSize(1);
    }

    @Test
    void listarSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(formalizacionGateway.listar(any())).thenReturn(List.of(oc()));

        assertThat(service.listar(null)).hasSize(1);
        verify(formalizacionGateway).listar(eq(new FormalizacionFiltro(15, null, null, null)));
    }

    @Test
    void listarConservaElEjercicioIndicado() {
        when(formalizacionGateway.listar(new FormalizacionFiltro(14, 7, null, null))).thenReturn(List.of());

        assertThat(service.listar(new FormalizacionFiltro(14, 7, null, null))).isEmpty();
    }

    @Test
    void listarConFiltroParcialCompletaElEjercicio() {
        java.time.LocalDateTime desde = java.time.LocalDateTime.of(2026, 10, 1, 0, 0);
        java.time.LocalDateTime hasta = java.time.LocalDateTime.of(2026, 10, 31, 23, 59);
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(formalizacionGateway.listar(any())).thenReturn(List.of());

        assertThat(service.listar(new FormalizacionFiltro(null, 7, desde, hasta))).isEmpty();
        verify(formalizacionGateway).listar(eq(new FormalizacionFiltro(15, 7, desde, hasta)));
    }

    @Test
    void getByIdDelega() {
        when(formalizacionGateway.getById(1L)).thenReturn(oc());
        assertThat(service.getById(1L).numero()).isEqualTo("OC-2026-000001");
    }

    @Test
    void porCotizacionDelega() {
        when(formalizacionGateway.porCotizacion(1L)).thenReturn(Optional.of(oc()));
        assertThat(service.porCotizacion(1L)).isPresent();
    }

    @Test
    void formalizarDelegaConElActor() {
        FormalizacionSolicitud solicitud = new FormalizacionSolicitud(1L, null, null, null, null, null, null,
                null, null);
        when(formalizacionGateway.formalizar(solicitud, 38L)).thenReturn(oc());

        assertThat(service.formalizar(solicitud, 38L).numero()).isEqualTo("OC-2026-000001");
    }
}
