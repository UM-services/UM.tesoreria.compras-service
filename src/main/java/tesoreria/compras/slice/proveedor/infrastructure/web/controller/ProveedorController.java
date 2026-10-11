package tesoreria.compras.slice.proveedor.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.application.service.ProveedorService;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorRequest;
import tesoreria.compras.slice.proveedor.infrastructure.web.dto.ProveedorResponse;
import tesoreria.compras.slice.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import java.util.List;

/**
 * Fachada de proveedores (pantalla Proveedores). Cada endpoint exige su clave de
 * permiso (ver {@link RequierePermiso}); el core queda intacto y sin gating.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/proveedor")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;

    @RequierePermiso("compras.proveedores")
    @PostMapping("/page")
    public ResponseEntity<PaginatedResponse<ProveedorResponse>> findPaginated(
            @RequestBody(required = false) PageRequest pageRequest) {
        int page = pageRequest == null || pageRequest.page() == null ? 0 : pageRequest.page();
        int size = pageRequest == null || pageRequest.size() == null ? 20 : pageRequest.size();
        return ResponseEntity.ok(
                proveedorDtoMapper.toPaginatedResponse(proveedorService.getPaginated(page, size)));
    }

    @RequierePermiso("compras.proveedores")
    @PostMapping("/search")
    public ResponseEntity<List<ProveedorResponse>> search(@RequestBody List<String> conditions) {
        List<ProveedorResponse> responses = proveedorService.search(conditions).stream()
                .map(proveedorDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @RequierePermiso("compras.proveedores")
    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<ProveedorResponse> findByCuit(@PathVariable String cuit) {
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedorService.getByCuit(cuit)));
    }

    @RequierePermiso("compras.proveedores")
    @GetMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> findById(@PathVariable Integer proveedorId) {
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedorService.getProveedorById(proveedorId)));
    }

    @RequierePermiso("compras.proveedores.crear")
    @PostMapping("/")
    public ResponseEntity<ProveedorResponse> create(@RequestBody ProveedorRequest request) {
        var created = proveedorService.create(proveedorDtoMapper.toDomain(request));
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(created));
    }

    @RequierePermiso("compras.proveedores.editar")
    @PutMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> update(@PathVariable Integer proveedorId,
                                                    @RequestBody ProveedorRequest request) {
        var updated = proveedorService.update(proveedorId, proveedorDtoMapper.toDomain(request));
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(updated));
    }

    @RequierePermiso("compras.proveedores.eliminar")
    @DeleteMapping("/{proveedorId}")
    public ResponseEntity<Void> delete(@PathVariable Integer proveedorId) {
        proveedorService.delete(proveedorId);
        return ResponseEntity.noContent().build();
    }
}
