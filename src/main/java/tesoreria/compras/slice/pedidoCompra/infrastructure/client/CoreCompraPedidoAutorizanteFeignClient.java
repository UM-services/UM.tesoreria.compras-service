package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraPedidoAutorizanteClient")
public interface CoreCompraPedidoAutorizanteFeignClient {

    @GetMapping("/api/tesoreria/core/compraPedidoAutorizante/dependencias/{autorizanteId}")
    CoreCompraPedidoAutorizanteResponse getDependencias(@PathVariable("autorizanteId") Long autorizanteId);
}
