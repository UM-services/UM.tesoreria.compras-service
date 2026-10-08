package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesoreria.compras.slice.pedidoCompra.application.service.PedidoCompraService;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompraFiltro;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.*;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Fachada del pedido de compra. Ruta canónica {@code /api/tesoreria/compras/pedido}.
 * La identidad llega por el header {@code X-User-Id} (transitorio, hasta el JWT de M2).
 */
@RestController
@RequestMapping("/api/tesoreria/compras/pedido")
@RequiredArgsConstructor
public class PedidoCompraController {

    static final String USER_ID_HEADER = "X-User-Id";

    private final PedidoCompraService pedidoCompraService;
    private final PedidoCompraDtoMapper pedidoCompraDtoMapper;

    @GetMapping("/iniciar")
    public ResponseEntity<ContextoInicioPedidoResponse> iniciar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(pedidoCompraService.getContexto(userId)));
    }

    @GetMapping("/bandeja")
    public ResponseEntity<List<PedidoCompraResponse>> bandeja(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(pedidoCompraService.bandeja(userId, estado).stream()
                .map(pedidoCompraDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/consulta")
    public ResponseEntity<List<PedidoCompraResponse>> consulta(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer solicitanteId,
            @RequestParam(required = false) Integer dependenciaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        PedidoCompraFiltro filtro = new PedidoCompraFiltro(estado, solicitanteId, dependenciaId, null,
                fechaDesde, fechaHasta);
        return ResponseEntity.ok(pedidoCompraService.consulta(userId, filtro).stream()
                .map(pedidoCompraDtoMapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PedidoCompraResponse> crear(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @RequestBody PedidoCompraRequest request) {
        PedidoCompra pedido = pedidoCompraDtoMapper.toDomain(request);
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.crear(userId, pedido, request.enviar())));
    }

    @PutMapping("/{compraPedidoId}")
    public ResponseEntity<PedidoCompraResponse> actualizar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId,
            @RequestBody PedidoCompraRequest request) {
        PedidoCompra pedido = pedidoCompraDtoMapper.toDomain(request);
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.actualizar(userId, compraPedidoId, pedido, request.enviar())));
    }

    @PostMapping("/{compraPedidoId}/enviar")
    public ResponseEntity<PedidoCompraResponse> enviar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.enviar(userId, compraPedidoId)));
    }

    @PostMapping("/{compraPedidoId}/descartar")
    public ResponseEntity<PedidoCompraResponse> descartar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId,
            @RequestBody DescartarPedidoRequest request) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.descartar(userId, compraPedidoId,
                        request == null ? null : request.motivo())));
    }

    @PostMapping("/{compraPedidoId}/aprobar")
    public ResponseEntity<PedidoCompraResponse> aprobar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.aprobar(userId, compraPedidoId)));
    }

    @PostMapping("/{compraPedidoId}/rechazar")
    public ResponseEntity<PedidoCompraResponse> rechazar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId,
            @RequestBody RechazarPedidoRequest request) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.rechazar(userId, compraPedidoId,
                        request == null ? null : request.motivo())));
    }

    @GetMapping
    public ResponseEntity<List<PedidoCompraResponse>> listar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        List<PedidoCompraResponse> responses = pedidoCompraService.listar(userId).stream()
                .map(pedidoCompraDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{compraPedidoId}")
    public ResponseEntity<PedidoCompraResponse> findById(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(
                pedidoCompraService.getById(userId, compraPedidoId)));
    }

    @GetMapping("/{compraPedidoId}/historial")
    public ResponseEntity<List<PedidoCompraHistorialResponse>> historial(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
            @PathVariable Integer compraPedidoId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponseHistorial(
                pedidoCompraService.historial(userId, compraPedidoId)));
    }
}
