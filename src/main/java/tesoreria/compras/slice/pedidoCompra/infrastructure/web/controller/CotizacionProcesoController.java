package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionProcesoService;
import tesoreria.compras.slice.pedidoCompra.domain.model.CotizacionProcesoFiltro;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoProveedorResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoRespuestaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecomendarCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionProcesoDtoMapper;

import java.util.List;

/**
 * Fachada de los procesos de cotización. Ruta canónica
 * {@code /api/tesoreria/compras/cotizacion}. Permiso {@code compras.cotizaciones.gestionar}.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/cotizacion")
@RequiredArgsConstructor
@RequierePermiso("compras.cotizaciones.gestionar")
public class CotizacionProcesoController {

    private final CotizacionProcesoService cotizacionProcesoService;
    private final CotizacionProcesoDtoMapper cotizacionProcesoDtoMapper;

    @GetMapping("/pendientes")
    public ResponseEntity<List<CotizacionPendienteResponse>> pendientes(
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) List<Integer> dependenciaIds) {
        return ResponseEntity.ok(cotizacionProcesoService.pendientes(ejercicioId, dependenciaIds).stream()
                .map(cotizacionProcesoDtoMapper::toResponse)
                .toList());
    }

    /** Bandeja transversal "Cotizaciones pedidas": documentos de proveedor en ENVIADA. */
    @GetMapping("/proveedores-pendientes")
    public ResponseEntity<List<CotizacionProcesoProveedorPendienteResponse>> proveedoresPendientes(
            @RequestParam(required = false) Integer ejercicioId) {
        return ResponseEntity.ok(cotizacionProcesoService.respuestasPendientes(ejercicioId).stream()
                .map(cotizacionProcesoDtoMapper::toResponse)
                .toList());
    }

    @GetMapping
    public ResponseEntity<List<CotizacionProcesoResponse>> listar() {
        return ResponseEntity.ok(cotizacionProcesoService.listar(
                        new CotizacionProcesoFiltro(null, null, null, null)).stream()
                .map(cotizacionProcesoDtoMapper::toResponse)
                .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<CotizacionProcesoResponse>> search(@RequestBody CotizacionProcesoCriteriaRequest request) {
        CotizacionProcesoFiltro filtro = new CotizacionProcesoFiltro(request.estado(), request.ejercicioId(),
                request.fechaDesde(), request.fechaHasta());
        return ResponseEntity.ok(cotizacionProcesoService.listar(filtro).stream()
                .map(cotizacionProcesoDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{compraCotizacionId}")
    public ResponseEntity<CotizacionProcesoResponse> findById(@PathVariable Long compraCotizacionId) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.getById(compraCotizacionId)));
    }

    @PostMapping
    public ResponseEntity<CotizacionProcesoResponse> crear(@RequestBody CotizacionProcesoRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.crear(cotizacionProcesoDtoMapper.toDomain(request))));
    }

    @PutMapping("/{compraCotizacionId}")
    public ResponseEntity<CotizacionProcesoResponse> actualizar(@PathVariable Long compraCotizacionId,
                                                                @RequestBody CotizacionProcesoRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.actualizar(compraCotizacionId, cotizacionProcesoDtoMapper.toDomain(request))));
    }

    @PostMapping("/{compraCotizacionId}/enviar")
    public ResponseEntity<CotizacionProcesoResponse> enviar(@PathVariable Long compraCotizacionId) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.enviar(compraCotizacionId)));
    }

    @PostMapping("/{compraCotizacionId}/anular")
    public ResponseEntity<CotizacionProcesoResponse> anular(@PathVariable Long compraCotizacionId,
                                                            @RequestBody(required = false) MotivoRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.anular(compraCotizacionId, request == null ? null : request.motivo())));
    }

    // ---- Etapa 6C ----

    @PostMapping("/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/respuesta")
    public ResponseEntity<CotizacionProcesoProveedorResponse> registrarRespuesta(
            @PathVariable Long compraCotizacionId,
            @PathVariable Long compraCotizacionProveedorId,
            @RequestBody CotizacionProcesoRespuestaRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.registrarRespuesta(compraCotizacionId, compraCotizacionProveedorId,
                        cotizacionProcesoDtoMapper.toDomainRespuesta(request))));
    }

    @PostMapping("/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/no-cotiza")
    public ResponseEntity<CotizacionProcesoProveedorResponse> marcarNoCotiza(
            @PathVariable Long compraCotizacionId,
            @PathVariable Long compraCotizacionProveedorId,
            @RequestBody(required = false) MotivoRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.marcarNoCotiza(compraCotizacionId, compraCotizacionProveedorId,
                        request == null ? null : request.motivo())));
    }

    @GetMapping("/{compraCotizacionId}/comparacion")
    public ResponseEntity<CotizacionProcesoResponse> comparacion(@PathVariable Long compraCotizacionId) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.comparacion(compraCotizacionId)));
    }

    @PostMapping("/{compraCotizacionId}/recomendar")
    public ResponseEntity<CotizacionProcesoResponse> recomendar(@PathVariable Long compraCotizacionId,
                                                                @RequestBody RecomendarCotizacionRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.recomendar(compraCotizacionId,
                        request.compraCotizacionProveedorId(), request.fundamento())));
    }

    @PostMapping("/{compraCotizacionId}/cerrar")
    public ResponseEntity<CotizacionProcesoResponse> cerrar(@PathVariable Long compraCotizacionId) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(
                cotizacionProcesoService.cerrar(compraCotizacionId)));
    }

    public record MotivoRequest(String motivo) {
    }
}
