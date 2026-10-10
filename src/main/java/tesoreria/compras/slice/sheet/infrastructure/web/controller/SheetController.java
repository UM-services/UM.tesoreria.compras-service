package tesoreria.compras.slice.sheet.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.sheet.application.service.SheetService;

@RestController
@RequestMapping("/api/tesoreria/compras/sheet")
@RequiredArgsConstructor
public class SheetController {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final SheetService sheetService;

    @RequierePermiso("compras.proveedores.descargar")
    @GetMapping("/generateProveedores")
    public ResponseEntity<byte[]> generateProveedores() {
        byte[] content = sheetService.generateProveedores();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proveedores.xlsx\"")
                .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                .body(content);
    }
}
