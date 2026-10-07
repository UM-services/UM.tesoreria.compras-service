package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreUsuarioClient")
public interface CoreUsuarioFeignClient {

    @GetMapping("/api/tesoreria/core/auth/me/{userId}")
    CoreUsuarioResponse getMe(@PathVariable("userId") Long userId);
}
