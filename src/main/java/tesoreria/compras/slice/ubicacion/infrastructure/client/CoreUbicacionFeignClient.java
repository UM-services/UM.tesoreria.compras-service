package tesoreria.compras.slice.ubicacion.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreUbicacionClient")
public interface CoreUbicacionFeignClient {

    @GetMapping("/api/tesoreria/core/ubicacion/")
    List<CoreUbicacionResponse> getUbicaciones();
}
