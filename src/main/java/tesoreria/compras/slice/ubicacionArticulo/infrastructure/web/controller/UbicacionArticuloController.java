package tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.ubicacionArticulo.application.service.UbicacionArticuloService;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloResponse;
import tesoreria.compras.slice.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import java.util.List;

/**
 * Fachada de imputaciones ubicación↔artículo (sección Imputaciones de la pantalla
 * Gastos).
 */
@RestController
@RequestMapping("/api/tesoreria/compras/ubicacionArticulo")
@RequiredArgsConstructor
public class UbicacionArticuloController {

    private final UbicacionArticuloService ubicacionArticuloService;
    private final UbicacionArticuloDtoMapper ubicacionArticuloDtoMapper;

    @RequierePermiso("compras.gastos")
    @GetMapping("/articulo/{articuloId}")
    public ResponseEntity<List<UbicacionArticuloResponse>> findByArticulo(@PathVariable Long articuloId) {
        List<UbicacionArticuloResponse> responses = ubicacionArticuloService.getByArticulo(articuloId).stream()
                .map(ubicacionArticuloDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @RequierePermiso("compras.gastos.imputar")
    @PostMapping("/")
    public ResponseEntity<UbicacionArticuloResponse> save(@RequestBody UbicacionArticuloRequest request) {
        var saved = ubicacionArticuloService.save(ubicacionArticuloDtoMapper.toDomain(request));
        return ResponseEntity.ok(ubicacionArticuloDtoMapper.toResponse(saved));
    }
}
