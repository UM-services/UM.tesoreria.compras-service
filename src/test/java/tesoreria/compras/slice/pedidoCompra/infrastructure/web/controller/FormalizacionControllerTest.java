package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.pedidoCompra.application.service.FormalizacionService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.Formalizacion;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionPendiente;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.FormalizacionDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormalizacionControllerTest {

    @Mock private FormalizacionService service;

    private FormalizacionController controller;

    @BeforeEach
    void setUp() {
        controller = new FormalizacionController(service, new FormalizacionDtoMapper());
    }

    private Formalizacion oc() {
        return new Formalizacion(1L, 9L, "OC-2026-000001", null, 15, null, 7, "ARS",
                new BigDecimal("28000.00"), null, null, null, null, null, 38, List.of(), null, null);
    }

    private FormalizacionRequest request() {
        return new FormalizacionRequest(1L, "OC-EXT", null, null, null, null, null, null, null);
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(15)).thenReturn(List.of(new FormalizacionPendiente(1L, "SC-1", 15, 9L, 5L, 7,
                new BigDecimal("28000.00"), "ARS", null, null, null, List.of())));

        var response = controller.pendientes(15);

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).compraCotizacionId()).isEqualTo(1L);
    }

    @Test
    void listarDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(oc()));
        assertThat(controller.listar(15, null).getBody()).hasSize(1);
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(oc()));
        assertThat(controller.search(new FormalizacionCriteriaRequest(15, null, null, null)).getBody()).hasSize(1);
    }

    @Test
    void findByIdDevuelveLaOc() {
        when(service.getById(1L)).thenReturn(oc());
        assertThat(controller.findById(1L).getBody().numero()).isEqualTo("OC-2026-000001");
    }

    @Test
    void porCotizacionDevuelveLaOc() {
        when(service.porCotizacion(1L)).thenReturn(Optional.of(oc()));
        assertThat(controller.porCotizacion(1L).getBody().numero()).isEqualTo("OC-2026-000001");
    }

    @Test
    void porCotizacionInexistenteFalla() {
        when(service.porCotizacion(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.porCotizacion(1L)).isInstanceOf(FormalizacionNotFoundException.class);
    }

    @Test
    void formalizarDelegaConElActor() {
        when(service.formalizar(any(), eq(38L))).thenReturn(oc());

        assertThat(controller.formalizar(38L, request()).getBody().numero()).isEqualTo("OC-2026-000001");
    }
}
