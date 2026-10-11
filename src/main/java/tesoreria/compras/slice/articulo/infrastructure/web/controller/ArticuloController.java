package tesoreria.compras.slice.articulo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import tesoreria.compras.slice.articulo.application.service.ArticuloService;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloRequest;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloSearchResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.util.List;

/**
 * Fachada de artículos (pantalla Gastos). Cada endpoint exige su clave de permiso
 * (ver {@link RequierePermiso}); el core queda intacto y sin gating.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/articulo")
@RequiredArgsConstructor
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ArticuloDtoMapper articuloDtoMapper;

    @RequierePermiso("compras.gastos")
    @GetMapping("/{articuloId}")
    public ResponseEntity<ArticuloResponse> getArticuloById(@PathVariable Long articuloId) {
        return ResponseEntity.ok(articuloDtoMapper.toResponse(articuloService.getArticuloById(articuloId)));
    }

    @RequierePermiso("compras.gastos")
    @PostMapping("/search")
    public ResponseEntity<List<ArticuloSearchResponse>> searchArticulos(@RequestBody List<String> conditions) {
        List<ArticuloSearchResponse> responses = articuloService.searchArticulos(conditions).stream()
                .map(articuloDtoMapper::toSearchResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @RequierePermiso("compras.gastos")
    @PostMapping("/tipo/{tipo}/page")
    public ResponseEntity<PaginatedResponse<ArticuloResponse>> getPaginatedByTipo(
            @PathVariable String tipo,
            @RequestBody(required = false) PageRequest pageRequest) {
        int page = pageRequest == null || pageRequest.page() == null ? 0 : pageRequest.page();
        int size = pageRequest == null || pageRequest.size() == null ? 10 : pageRequest.size();
        return ResponseEntity.ok(
                articuloDtoMapper.toPaginatedResponse(articuloService.getPaginated(tipo, page, size)));
    }

    @RequierePermiso("compras.gastos.crear")
    @GetMapping("/new")
    public ResponseEntity<ArticuloResponse> getNewArticulo() {
        return ResponseEntity.ok(articuloDtoMapper.toResponse(articuloService.getNewArticulo()));
    }

    @RequierePermiso("compras.gastos.crear")
    @PostMapping("/")
    public ResponseEntity<ArticuloResponse> createArticulo(@RequestBody ArticuloRequest request) {
        var created = articuloService.createArticulo(articuloDtoMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloDtoMapper.toResponse(created));
    }

    @RequierePermiso("compras.gastos.editar")
    @PutMapping("/{articuloId}")
    public ResponseEntity<ArticuloResponse> updateArticulo(@PathVariable Long articuloId,
                                                           @RequestBody ArticuloRequest request) {
        var updated = articuloService.updateArticulo(articuloId, articuloDtoMapper.toDomain(request));
        return ResponseEntity.ok(articuloDtoMapper.toResponse(updated));
    }

    @RequierePermiso("compras.gastos.eliminar")
    @DeleteMapping("/{articuloId}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable Long articuloId) {
        articuloService.deleteArticulo(articuloId);
        return ResponseEntity.noContent().build();
    }
}
