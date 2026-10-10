package tesoreria.compras.slice.sheet.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.sheet.application.service.SheetService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SheetControllerTest {

    @Mock
    private SheetService sheetService;

    @Test
    void returnsXlsxBytes() {
        when(sheetService.generateProveedores()).thenReturn(new byte[]{1, 2});

        var response = new SheetController(sheetService).generateProveedores();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(1, 2);
        assertThat(response.getHeaders().getContentType().toString())
                .contains("spreadsheetml.sheet");
        assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("proveedores.xlsx");
        verify(sheetService).generateProveedores();
    }
}
