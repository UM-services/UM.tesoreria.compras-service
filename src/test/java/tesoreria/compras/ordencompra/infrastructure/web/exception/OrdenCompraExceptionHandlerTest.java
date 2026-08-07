package tesoreria.compras.ordencompra.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.ordencompra.application.service.OrdenCompraService;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraInvalidaException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNoEditableException;
import tesoreria.compras.ordencompra.domain.exception.OrdenCompraNotFoundException;
import tesoreria.compras.ordencompra.domain.exception.TransicionInvalidaException;
import tesoreria.compras.ordencompra.domain.model.OrdenCompraEstado;
import tesoreria.compras.ordencompra.infrastructure.web.controller.OrdenCompraController;
import tesoreria.compras.ordencompra.infrastructure.web.mapper.OrdenCompraDtoMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdenCompraExceptionHandlerTest {

    private static final String RUTA = "/api/tesoreria/compras/ordenCompra";

    @Mock
    private OrdenCompraService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrdenCompraController(service, new OrdenCompraDtoMapper()))
                .setControllerAdvice(new OrdenCompraExceptionHandler())
                .build();
    }

    @Test
    void mapsMissingOrdenToNotFound() throws Exception {
        when(service.get(1L)).thenThrow(new OrdenCompraNotFoundException(1L));

        mockMvc.perform(get(RUTA + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe la orden de compra 1"));
    }

    @Test
    void mapsDomainValidationToBadRequest() throws Exception {
        when(service.get(1L)).thenThrow(new OrdenCompraInvalidaException("La orden requiere cabecera e ítems"));

        mockMvc.perform(get(RUTA + "/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La orden requiere cabecera e ítems"));
    }

    @Test
    void mapsNonEditableOrdenToConflict() throws Exception {
        when(service.get(1L)).thenThrow(new OrdenCompraNoEditableException(OrdenCompraEstado.APROBADA));

        mockMvc.perform(get(RUTA + "/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail")
                        .value("Sólo se puede editar una orden pendiente de aprobación; su estado es APROBADA"));
    }

    @Test
    void mapsForbiddenTransitionToConflict() throws Exception {
        when(service.transition(any(), any()))
                .thenThrow(new TransicionInvalidaException(OrdenCompraEstado.CUMPLIDA, OrdenCompraEstado.ANULADA));

        mockMvc.perform(post(RUTA + "/1/anular"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("No se puede pasar de CUMPLIDA a ANULADA"));
    }
}
