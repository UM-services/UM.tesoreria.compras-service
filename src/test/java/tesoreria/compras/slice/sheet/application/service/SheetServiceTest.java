package tesoreria.compras.slice.sheet.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.sheet.domain.ports.in.GenerateProveedoresSheetUseCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SheetServiceTest {

    @Mock
    private GenerateProveedoresSheetUseCase generateProveedoresSheetUseCase;

    @InjectMocks
    private SheetService sheetService;

    @Test
    void delegates() {
        when(generateProveedoresSheetUseCase.generateProveedores()).thenReturn(new byte[]{9});

        assertThat(sheetService.generateProveedores()).containsExactly(9);
    }
}
