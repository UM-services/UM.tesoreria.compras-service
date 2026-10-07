package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreDependenciaClient")
public interface CoreDependenciaFeignClient {

    @GetMapping("/api/tesoreria/core/dependencia/{dependenciaId}")
    CoreDependenciaResponse getById(@PathVariable("dependenciaId") Integer dependenciaId);
}
