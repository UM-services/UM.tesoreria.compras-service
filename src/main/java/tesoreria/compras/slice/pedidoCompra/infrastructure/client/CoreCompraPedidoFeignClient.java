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
    CoreCompraPedidoResponse enviar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                    @RequestBody CoreEnviarCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/aprobar")
    CoreCompraPedidoResponse aprobar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                     @RequestBody CoreAprobarCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/rechazar")
    CoreCompraPedidoResponse rechazar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                      @RequestBody CoreRechazarCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/descartar")
    CoreCompraPedidoResponse descartar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                       @RequestBody CoreDescartarCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/estimar")
    CoreCompraPedidoResponse estimar(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                     @RequestBody CoreEstimarCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/autorizarPresupuesto")
    CoreCompraPedidoResponse autorizarPresupuesto(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                                  @RequestBody CoreAutorizarPresupuestoCompraPedidoRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}/rechazarPresupuesto")
    CoreCompraPedidoResponse rechazarPresupuesto(@PathVariable("compraPedidoId") Integer compraPedidoId,
                                                 @RequestBody CoreRechazarPresupuestoCompraPedidoRequest request);

    @GetMapping("/api/tesoreria/core/compraPedido/{compraPedidoId}")
    CoreCompraPedidoResponse getById(@PathVariable("compraPedidoId") Integer compraPedidoId);

    @PostMapping("/api/tesoreria/core/compraPedido/search")
    List<CoreCompraPedidoResponse> search(@RequestBody CoreCompraPedidoSearchRequest request);

    @PostMapping("/api/tesoreria/core/compraPedido/search")
    List<CoreCompraPedidoResponse> listarPorSolicitante(@RequestBody CoreCompraPedidoSearchRequest request);
}
