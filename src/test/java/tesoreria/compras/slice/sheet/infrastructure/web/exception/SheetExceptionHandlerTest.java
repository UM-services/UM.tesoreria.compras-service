package tesoreria.compras.slice.sheet.infrastructure.web.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tesoreria.compras.slice.sheet.application.service.SheetService;
import tesoreria.compras.slice.sheet.domain.exception.SheetSourceUnavailableException;
import tesoreria.compras.slice.sheet.infrastructure.web.controller.SheetController;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SheetExceptionHandlerTest {

    @Mock
    private SheetService sheetService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SheetController(sheetService))
                .setControllerAdvice(new SheetExceptionHandler())
                .build();
    }

    @Test
    void mapsUnavailableSource() throws Exception {
        when(sheetService.generateProveedores()).thenThrow(new SheetSourceUnavailableException(new RuntimeException()));

        mockMvc.perform(get("/api/tesoreria/compras/sheet/generateProveedores"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("La generación de la planilla no está disponible"));
    }
}
