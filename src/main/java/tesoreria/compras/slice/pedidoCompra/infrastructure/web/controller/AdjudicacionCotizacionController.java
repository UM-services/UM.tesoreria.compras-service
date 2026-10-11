package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesoreria.compras.configuration.security.RequierePermiso;
import tesoreria.compras.slice.pedidoCompra.application.service.CotizacionProcesoService;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.AdjudicarCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionAdjudicacionResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CotizacionProcesoResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.FundamentoCotizacionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.LimiteAutorizacionResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.CotizacionProcesoDtoMapper;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

import java.util.List;

/**
 * Adjudicación de los procesos de cotización recomendados (etapa 7). Ruta canónica
 * {@code /api/tesoreria/compras/cotizacion/adjudicacion}. Permiso
 * {@code compras.adjudicacion.autorizar}. El límite por monto se valida en el servicio
 * (fail-closed) contra el total de la cotización recomendada.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/cotizacion/adjudicacion")
@RequiredArgsConstructor
@RequierePermiso(CotizacionProcesoService.PERMISO_ADJUDICAR)
public class AdjudicacionCotizacionController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final CotizacionProcesoService cotizacionProcesoService;
    private final CotizacionProcesoDtoMapper cotizacionProcesoDtoMapper;
    private final PedidoCompraDtoMapper pedidoCompraDtoMapper;

    @GetMapping("/bandeja")
    public ResponseEntity<List<CotizacionProcesoResponse>> bandeja(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        return ResponseEntity.ok(cotizacionProcesoService.adjudicacionBandeja(userId).stream()
                .map(cotizacionProcesoDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/limite")
    public ResponseEntity<LimiteAutorizacionResponse> limiteActual(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                cotizacionProcesoService.adjudicacionLimite(userId, null)));
    }

    @GetMapping("/limite/{ejercicioId}")
    public ResponseEntity<LimiteAutorizacionResponse> limite(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer ejercicioId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                cotizacionProcesoService.adjudicacionLimite(userId, ejercicioId)));
    }

    @PostMapping("/{compraCotizacionId}/adjudicar")
    public ResponseEntity<CotizacionAdjudicacionResponse> adjudicar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Long compraCotizacionId,
            @RequestBody(required = false) AdjudicarCotizacionRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(cotizacionProcesoService.adjudicar(
                userId, compraCotizacionId,
                request == null ? null : request.compraCotizacionProveedorId(),
                request == null ? null : request.fundamento())));
    }

    @PostMapping("/{compraCotizacionId}/devolver")
    public ResponseEntity<CotizacionAdjudicacionResponse> devolver(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Long compraCotizacionId,
            @RequestBody(required = false) FundamentoCotizacionRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(cotizacionProcesoService.devolver(
                userId, compraCotizacionId, request == null ? null : request.fundamento())));
    }

    @PostMapping("/{compraCotizacionId}/no-adjudicar")
    public ResponseEntity<CotizacionAdjudicacionResponse> noAdjudicar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Long compraCotizacionId,
            @RequestBody(required = false) FundamentoCotizacionRequest request) {
        return ResponseEntity.ok(cotizacionProcesoDtoMapper.toResponse(cotizacionProcesoService.noAdjudicar(
                userId, compraCotizacionId, request == null ? null : request.fundamento())));
    }

    @GetMapping("/{compraCotizacionId}")
    public ResponseEntity<CotizacionAdjudicacionResponse> adjudicacion(@PathVariable Long compraCotizacionId) {
        return cotizacionProcesoService.adjudicacion(compraCotizacionId)
                .map(cotizacionProcesoDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Adjudicación no encontrada para el proceso: " + compraCotizacionId));
    }
}
