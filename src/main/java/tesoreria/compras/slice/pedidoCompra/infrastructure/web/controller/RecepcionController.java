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
import tesoreria.compras.slice.pedidoCompra.application.service.RecepcionService;
import tesoreria.compras.slice.pedidoCompra.domain.exception.RecepcionNotFoundException;
import tesoreria.compras.slice.pedidoCompra.domain.model.RecepcionFiltro;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.CerrarRecepcionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionCriteriaRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionPendienteResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.RecepcionResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.RecepcionDtoMapper;

import java.util.List;

/**
 * Fachada de la recepción y conformidad (etapa 9). Ruta canónica
 * {@code /api/tesoreria/compras/recepcion}. Permiso {@code compras.recepcionar}.
 */
@RestController
@RequestMapping("/api/tesoreria/compras/recepcion")
@RequiredArgsConstructor
@RequierePermiso(RecepcionService.PERMISO_RECEPCIONAR)
public class RecepcionController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final RecepcionService recepcionService;
    private final RecepcionDtoMapper recepcionDtoMapper;

    /** Órdenes de Compra del ejercicio con recepción pendiente. */
    @GetMapping("/pendientes")
    public ResponseEntity<List<RecepcionPendienteResponse>> pendientes(
            @RequestParam(required = false) Integer ejercicioId) {
        return ResponseEntity.ok(recepcionService.pendientes(ejercicioId).stream()
                .map(recepcionDtoMapper::toResponse)
                .toList());
    }

    @GetMapping
    public ResponseEntity<List<RecepcionResponse>> listar(
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) Long compraFormalizacionId,
            @RequestParam(required = false) String resultado) {
        return ResponseEntity.ok(recepcionService.listar(new RecepcionFiltro(ejercicioId, compraFormalizacionId,
                        resultado, null, null)).stream()
                .map(recepcionDtoMapper::toResponse)
                .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecepcionResponse>> search(@RequestBody RecepcionCriteriaRequest request) {
        RecepcionFiltro filtro = new RecepcionFiltro(request.ejercicioId(), request.compraFormalizacionId(),
                request.resultado(), request.fechaDesde(), request.fechaHasta());
        return ResponseEntity.ok(recepcionService.listar(filtro).stream()
                .map(recepcionDtoMapper::toResponse)
                .toList());
    }

    /** Recepciones y saldos de una Orden de Compra. */
    @GetMapping("/por-formalizacion/{compraFormalizacionId}")
    public ResponseEntity<RecepcionPendienteResponse> porFormalizacion(
            @PathVariable Long compraFormalizacionId) {
        return recepcionService.porFormalizacion(compraFormalizacionId)
                .map(recepcionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RecepcionNotFoundException(
                        "No existe la Orden de Compra " + compraFormalizacionId));
    }

    @GetMapping("/{compraRecepcionId}")
    public ResponseEntity<RecepcionResponse> findById(@PathVariable Long compraRecepcionId) {
        return ResponseEntity.ok(recepcionDtoMapper.toResponse(recepcionService.getById(compraRecepcionId)));
    }

    /** Registra una entrega (total o parcial) contra una Orden de Compra. */
    @PostMapping
    public ResponseEntity<RecepcionResponse> registrar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestBody RecepcionRequest request) {
        return ResponseEntity.ok(recepcionDtoMapper.toResponse(
                recepcionService.registrar(recepcionDtoMapper.toDomain(request), userId)));
    }

    /** Cierre manual de la recepción de una OC con saldo abierto (motivo obligatorio). */
    @PostMapping("/formalizacion/{compraFormalizacionId}/cerrar")
    public ResponseEntity<Void> cerrar(@PathVariable Long compraFormalizacionId,
                                       @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                       @RequestBody CerrarRecepcionRequest request) {
        recepcionService.cerrar(compraFormalizacionId, request == null ? null : request.motivo(), userId);
        return ResponseEntity.noContent().build();
    }
}
