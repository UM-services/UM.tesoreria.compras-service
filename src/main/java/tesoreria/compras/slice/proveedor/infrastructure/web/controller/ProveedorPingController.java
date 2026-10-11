package tesoreria.compras.slice.proveedor.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.slice.proveedor.application.service.ProveedorService;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorResponse;
import tesoreria.compras.slice.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/compras/ping")
@RequiredArgsConstructor
public class ProveedorPingController {

    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;

    @GetMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> ping(@PathVariable Integer proveedorId) {
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedorService.getProveedorById(proveedorId)));
    }
}
