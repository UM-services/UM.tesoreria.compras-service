package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.application.service.RecepcionService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.Recepcion;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CerrarRecepcionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionItemRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.RecepcionDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecepcionControllerTest {

    @Mock private RecepcionService service;

    private RecepcionController controller;

    @BeforeEach
    void setUp() {
        controller = new RecepcionController(service, new RecepcionDtoMapper());
    }

    private Recepcion recepcion() {
        return new Recepcion(1L, 50L, "RC-2026-000001", 15, null, "R-1", "CONFORME", "NO_CORRESPONDE",
                null, 38, List.of(), null, null);
    }

    private RecepcionPendiente pendiente() {
        return new RecepcionPendiente(50L, "OC-2026-000003", null, 15, 7, null,
                new BigDecimal("28000.00"), "ARS", List.of(), List.of(), false);
    }

    private RecepcionRequest request() {
        return new RecepcionRequest(50L, null, "R-1", "INTERVENIDO", null,
                List.of(new RecepcionItemRequest(100L, 1, new BigDecimal("2.00"), "CONFORME", null)));
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(15)).thenReturn(List.of(pendiente()));

        var response = controller.pendientes(15);

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).compraFormalizacionId()).isEqualTo(50L);
    }

    @Test
    void listarDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(recepcion()));

        assertThat(controller.listar(15, 50L, "CONFORME").getBody()).hasSize(1);
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(recepcion()));

        assertThat(controller.search(new RecepcionCriteriaRequest(15, 50L, null, null, null)).getBody()).hasSize(1);
    }

    @Test
    void porFormalizacionDevuelveLosSaldos() {
        when(service.porFormalizacion(50L)).thenReturn(Optional.of(pendiente()));

        assertThat(controller.porFormalizacion(50L).getBody().numero()).isEqualTo("OC-2026-000003");
    }

    @Test
    void porFormalizacionInexistenteFalla() {
        when(service.porFormalizacion(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.porFormalizacion(50L))
                .isInstanceOf(RecepcionNotFoundException.class);
    }

    @Test
    void findByIdDevuelveLaRecepcion() {
        when(service.getById(1L)).thenReturn(recepcion());

        assertThat(controller.findById(1L).getBody().numero()).isEqualTo("RC-2026-000001");
    }

    @Test
    void registrarDelegaConElActor() {
        when(service.registrar(any(), eq(38L))).thenReturn(recepcion());

        assertThat(controller.registrar(38L, request()).getBody().numero()).isEqualTo("RC-2026-000001");
    }

    @Test
    void cerrarDelegaYDevuelve204() {
        var response = controller.cerrar(50L, 38L, new CerrarRecepcionRequest("El proveedor no completará el resto"));

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(service).cerrar(50L, "El proveedor no completará el resto", 38L);
    }

    @Test
    void cerrarSinCuerpoEnviaMotivoNulo() {
        controller.cerrar(50L, null, null);

        verify(service).cerrar(50L, null, null);
    }
}
