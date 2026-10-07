package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tesoreria.compras.slice.pedidoCompra.domain.model.PedidoCompra;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraPedidoClient")
public interface CoreCompraPedidoFeignClient {

    @PostMapping("/api/tesoreria/core/compraPedido")
    CoreCompraPedidoResponse crear(@RequestBody PedidoCompra pedido);

    @PutMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}")
    CoreCompraPedidoResponse actualizar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                        @RequestBody PedidoCompra pedido);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/enviar")
    CoreCompraPedidoResponse enviar(@PathVariable("compraPedidoId") Integer compraPedidoId);

    @GetMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}")
    CoreCompraPedidoResponse getById(@PathVariable("compraPedidoId") Integer compraPedidoId);

    @GetMapping("/api/tesoreria/core/compraPedido")
    List<CoreCompraPedidoResponse> listarPorSolicitante(@RequestParam("solicitanteId") Long solicitanteId);
}
