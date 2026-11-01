package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

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
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionTramoService;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionTramoResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionTramoDtoMapper;

import java.util.List;

/**
 * Fachada de configuración de tramos de cotizaciones mínimas por monto.
 * Ruta canónica {@code /api/tesoreria/compras/cotizacion/tramos}. El permiso
 * {@code compras.cotizaciones.configurar} lo exige {@link RequierePermiso} en el PEP.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/cotizacion/tramos")
@RequiredArgsConstructor
@RequierePermiso("compras.cotizaciones.configurar")
public class CotizacionTramoController {

    private final CotizacionTramoService cotizacionTramoService;
    private final CotizacionTramoDtoMapper cotizacionTramoDtoMapper;

    @GetMapping
    public ResponseEntity<List<CotizacionTramoResponse>> listar() {
        List<CotizacionTramoResponse> responses = cotizacionTramoService.listar().stream()
                .map(cotizacionTramoDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CotizacionTramoResponse> crear(@RequestBody CotizacionTramoRequest request) {
        return ResponseEntity.ok(cotizacionTramoDtoMapper.toResponse(
                cotizacionTramoService.crear(cotizacionTramoDtoMapper.toDomain(request))));
    }

    @PutMapping("/{cotizacionTramoId}")
    public ResponseEntity<CotizacionTramoResponse> actualizar(@PathVariable Long cotizacionTramoId,
                                                              @RequestBody CotizacionTramoRequest request) {
        return ResponseEntity.ok(cotizacionTramoDtoMapper.toResponse(
                cotizacionTramoService.actualizar(cotizacionTramoId, cotizacionTramoDtoMapper.toDomain(request))));
    }

    @DeleteMapping("/{cotizacionTramoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long cotizacionTramoId) {
        cotizacionTramoService.eliminar(cotizacionTramoId);
        return ResponseEntity.noContent().build();
    }
}
