package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.pedidoCompra.application.service.FormalizacionService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.FormalizacionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.FormalizacionFiltro;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FormalizacionResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.FormalizacionDtoMapper;

import java.util.List;

/**
 * Fachada de la Orden de Compra (etapa 8). Ruta canónica
 * {@code /api/tesoreria/compras/formalizacion}. Permiso {@code compras.formalizar}.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/formalizacion")
@RequiredArgsConstructor
@RequierePermiso(FormalizacionService.PERMISO_FORMALIZAR)
public class FormalizacionController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final FormalizacionService formalizacionService;
    private final FormalizacionDtoMapper formalizacionDtoMapper;

    /** Adjudicaciones pendientes de emitir la Orden de Compra. */
    @GetMapping("/pendientes")
    public ResponseEntity<List<FormalizacionPendienteResponse>> pendientes(
            @RequestParam(required = false) Integer ejercicioId) {
        return ResponseEntity.ok(formalizacionService.pendientes(ejercicioId).stream()
                .map(formalizacionDtoMapper::toResponse)
                .toList());
    }

    @GetMapping
    public ResponseEntity<List<FormalizacionResponse>> listar(
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) Integer proveedorId) {
        return ResponseEntity.ok(formalizacionService.listar(new FormalizacionFiltro(ejercicioId, proveedorId,
                        null, null)).stream()
                .map(formalizacionDtoMapper::toResponse)
                .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<FormalizacionResponse>> search(@RequestBody FormalizacionCriteriaRequest request) {
        FormalizacionFiltro filtro = new FormalizacionFiltro(request.ejercicioId(), request.proveedorId(),
                request.fechaDesde(), request.fechaHasta());
        return ResponseEntity.ok(formalizacionService.listar(filtro).stream()
                .map(formalizacionDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{compraFormalizacionId}")
    public ResponseEntity<FormalizacionResponse> findById(@PathVariable Long compraFormalizacionId) {
        return ResponseEntity.ok(formalizacionDtoMapper.toResponse(
                formalizacionService.getById(compraFormalizacionId)));
    }

    /** La OC emitida para un proceso de cotización, si existe. */
    @GetMapping("/por-cotizacion/{compraCotizacionId}")
    public ResponseEntity<FormalizacionResponse> porCotizacion(@PathVariable Long compraCotizacionId) {
        return formalizacionService.porCotizacion(compraCotizacionId)
                .map(formalizacionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new FormalizacionNotFoundException(compraCotizacionId, true));
    }

    /** Emite la Orden de Compra de una adjudicación. */
    @PostMapping
    public ResponseEntity<FormalizacionResponse> formalizar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestBody FormalizacionRequest request) {
        return ResponseEntity.ok(formalizacionDtoMapper.toResponse(
                formalizacionService.formalizar(formalizacionDtoMapper.toDomain(request), userId)));
    }
}
