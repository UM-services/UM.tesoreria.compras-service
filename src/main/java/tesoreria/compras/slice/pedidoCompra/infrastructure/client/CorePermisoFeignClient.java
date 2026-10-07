package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "corePermisoClient")
public interface CorePermisoFeignClient {

    @GetMapping("/api/tesoreria/core/permisoEfectivo/usuario/{userId}")
    CorePermisoEfectivoResponse getPermisosEfectivos(@PathVariable("userId") Long userId);
}
