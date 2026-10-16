package tesoreria.compras.slice.pedidoCompra.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionSolicitud;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.EjercicioActualGateway;
import tesoreria.compras.slice.pedidoCompra.domain.ports.out.RecepcionGateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepcionServiceTest {

    @Mock private RecepcionGateway recepcionGateway;
    @Mock private EjercicioActualGateway ejercicioActualGateway;

    @InjectMocks
    private RecepcionService service;

    private Recepcion recepcion() {
        return new Recepcion(1L, 50L, "RC-2026-000001", 15, null, "R-1", "CONFORME", "NO_CORRESPONDE",
                null, 38, List.of(), null, null);
    }

    private RecepcionPendiente pendiente() {
        return new RecepcionPendiente(50L, "OC-2026-000003", null, 15, 7, null,
                new BigDecimal("28000.00"), "ARS", List.of(), List.of(), false);
    }

    @Test
    void pendientesDelegaConElEjercicioIndicado() {
        when(recepcionGateway.pendientes(15)).thenReturn(List.of());

        assertThat(service.pendientes(15)).isEmpty();
    }

    @Test
    void pendientesSinEjercicioUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(recepcionGateway.pendientes(15)).thenReturn(List.of(pendiente()));

        assertThat(service.pendientes(null)).hasSize(1);
    }

    @Test
    void listarSinEjercicioNiOrdenDeCompraUsaElActual() {
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(recepcionGateway.listar(any())).thenReturn(List.of(recepcion()));

        assertThat(service.listar(new RecepcionFiltro(null, null, null, null, null))).hasSize(1);
        verify(recepcionGateway).listar(eq(new RecepcionFiltro(15, null, null, null, null)));
    }

    @Test
    void listarConOrdenDeCompraNoResuelveElEjercicio() {
        RecepcionFiltro filtro = new RecepcionFiltro(null, 50L, null, null, null);
        when(recepcionGateway.listar(filtro)).thenReturn(List.of());

        assertThat(service.listar(filtro)).isEmpty();
    }

    @Test
    void listarConservaElEjercicioIndicado() {
        RecepcionFiltro filtro = new RecepcionFiltro(14, 50L, "CONFORME", null, null);
        when(recepcionGateway.listar(filtro)).thenReturn(List.of());

        assertThat(service.listar(filtro)).isEmpty();
    }

    @Test
    void listarConFiltroNuloNoRompe() {
        when(recepcionGateway.listar(any())).thenReturn(List.of());

        assertThat(service.listar(null)).isEmpty();
        verify(recepcionGateway).listar(null);
    }

    @Test
    void listarCompletaElEjercicioPreservandoLosDemasFiltros() {
        LocalDateTime desde = LocalDateTime.of(2026, 10, 1, 0, 0);
        when(ejercicioActualGateway.getEjercicioActualId()).thenReturn(15);
        when(recepcionGateway.listar(any())).thenReturn(List.of());

        assertThat(service.listar(new RecepcionFiltro(null, null, "CONFORME", desde, null))).isEmpty();
        verify(recepcionGateway).listar(eq(new RecepcionFiltro(15, null, "CONFORME", desde, null)));
    }

    @Test
    void getByIdDelega() {
        when(recepcionGateway.getById(1L)).thenReturn(recepcion());

        assertThat(service.getById(1L).numero()).isEqualTo("RC-2026-000001");
    }

    @Test
    void porFormalizacionDelega() {
        when(recepcionGateway.porFormalizacion(50L)).thenReturn(Optional.of(pendiente()));

        assertThat(service.porFormalizacion(50L)).isPresent();
    }

    @Test
    void registrarDelegaConElActor() {
        RecepcionSolicitud solicitud = new RecepcionSolicitud(50L, null, "R-1", null, null, List.of());
        when(recepcionGateway.registrar(solicitud, 38L)).thenReturn(recepcion());

        assertThat(service.registrar(solicitud, 38L).numero()).isEqualTo("RC-2026-000001");
        verify(recepcionGateway).registrar(solicitud, 38L);
    }

    @Test
    void cerrarDelega() {
        service.cerrar(50L, "motivo", 38L);

        verify(recepcionGateway).cerrar(50L, "motivo", 38L);
    }
}
