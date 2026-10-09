package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraPedidoHistorialClient")
public interface CoreCompraPedidoHistorialFeignClient {

    @GetMapping("/api/tesoreria/core/compraPedidoHistorial/{compraPedidoId}")
    List<CoreCompraPedidoHistorialResponse> listar(@PathVariable("compraPedidoId") Integer compraPedidoId);
}
