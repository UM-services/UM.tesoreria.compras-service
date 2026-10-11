package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "tesoreria-core-service", contextId = "coreEjercicioClient")
public interface CoreEjercicioFeignClient {

    @GetMapping("/ejercicio/last")
    CoreEjercicioResponse getLast();
}
