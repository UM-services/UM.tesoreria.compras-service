package tesoreria.compras.slice.proveedor.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreProveedorClient")
public interface CoreProveedorFeignClient {

    @GetMapping("/api/tesoreria/core/proveedor/{proveedorId}")
    CoreProveedorResponse getProveedorById(@PathVariable("proveedorId") Integer proveedorId);
}
