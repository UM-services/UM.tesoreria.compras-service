package tesoreria.compras.slice.sheet.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "tesoreria-core-service", contextId = "coreSheetClient")
public interface CoreSheetFeignClient {

    @GetMapping("/api/tesoreria/core/sheet/generateProveedores")
    byte[] generateProveedores();
}
