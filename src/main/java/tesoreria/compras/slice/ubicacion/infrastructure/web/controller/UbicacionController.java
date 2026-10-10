package tesoreria.compras.slice.ubicacion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.ubicacion.application.service.UbicacionService;
import tesoreria.compras.slice.ubicacion.infrastructure.web.dto.UbicacionResponse;
import tesoreria.compras.slice.ubicacion.infrastructure.web.mapper.UbicacionDtoMapper;

import java.util.List;

/**
 * Fachada de ubicaciones (selector de imputación de la pantalla Gastos).
 */
@RestController
@RequestMapping("/api/tesoreria/compras/ubicacion")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;
    private final UbicacionDtoMapper ubicacionDtoMapper;

    @RequierePermiso("compras.gastos")
    @GetMapping("/")
    public ResponseEntity<List<UbicacionResponse>> findAll() {
        List<UbicacionResponse> responses = ubicacionService.getUbicaciones().stream()
                .map(ubicacionDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
