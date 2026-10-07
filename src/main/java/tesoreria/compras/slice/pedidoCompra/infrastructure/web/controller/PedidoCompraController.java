package tesoreria.compras.slice.pedidoCompra.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesoreria.compras.slice.pedidoCompra.application.service.PedidoCompraService;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.ContextoInicioPedidoResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraRequest;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.dto.PedidoCompraResponse;
import tesoreria.compras.slice.pedidoCompra.infrastructure.web.mapper.PedidoCompraDtoMapper;

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

    @GetMapping("/{compraPedidoId}")
    public ResponseEntity<PedidoCompraResponse> findById(@PathVariable Integer compraPedidoId) {
        return ResponseEntity.ok(pedidoCompraDtoMapper.toResponse(pedidoCompraService.getById(compraPedidoId)));
    }

    @GetMapping
    public ResponseEntity<List<PedidoCompraResponse>> listar(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        List<PedidoCompraResponse> responses = pedidoCompraService.listar(userId).stream()
                .map(pedidoCompraDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
