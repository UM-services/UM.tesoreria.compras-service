package tesoreria.compras.slice.sheet.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tesoreria.compras.slice.sheet.domain.ports.in.GenerateProveedoresSheetUseCase;
import tesoreria.compras.slice.sheet.domain.ports.out.SheetGateway;

@Component
@RequiredArgsConstructor
public class GenerateProveedoresSheetUseCaseImpl implements GenerateProveedoresSheetUseCase {

    private final SheetGateway sheetGateway;

    @Override
    public byte[] generateProveedores() {
        return sheetGateway.generateProveedores();
    }
}
