package tesoreria.compras.slice.sheet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tesoreria.compras.slice.sheet.domain.ports.in.GenerateProveedoresSheetUseCase;

@Service
@RequiredArgsConstructor
public class SheetService {

    private final GenerateProveedoresSheetUseCase generateProveedoresSheetUseCase;

    public byte[] generateProveedores() {
        return generateProveedoresSheetUseCase.generateProveedores();
    }
}
