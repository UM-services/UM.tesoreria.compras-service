package tesoreria.compras.slice.sheet.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tesoreria.compras.slice.sheet.domain.ports.out.SheetGateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateProveedoresSheetUseCaseImplTest {

    @Mock
    private SheetGateway sheetGateway;

    @Test
    void delegates() {
        when(sheetGateway.generateProveedores()).thenReturn(new byte[]{1, 2, 3});

        var result = new GenerateProveedoresSheetUseCaseImpl(sheetGateway).generateProveedores();

        assertThat(result).containsExactly(1, 2, 3);
        verify(sheetGateway).generateProveedores();
    }
}
