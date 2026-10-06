package tesoreria.compras.slice.articulo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.slice.articulo.application.service.ArticuloService;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.dto.ArticuloSearchResponse;
import tesoreria.compras.slice.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/compras/articulo")
@RequiredArgsConstructor
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ArticuloDtoMapper articuloDtoMapper;

    @GetMapping("/{articuloId}")
    public ResponseEntity<ArticuloResponse> getArticuloById(@PathVariable Long articuloId) {
        return ResponseEntity.ok(articuloDtoMapper.toResponse(articuloService.getArticuloById(articuloId)));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ArticuloSearchResponse>> searchArticulos(@RequestBody List<String> conditions) {
        List<ArticuloSearchResponse> responses = articuloService.searchArticulos(conditions).stream()
                .map(articuloDtoMapper::toSearchResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
