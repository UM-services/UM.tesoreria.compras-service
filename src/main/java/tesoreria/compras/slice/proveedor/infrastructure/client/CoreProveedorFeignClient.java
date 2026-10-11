package tesoreria.compras.slice.proveedor.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.proveedor.domain.model.Proveedor;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreProveedorClient")
public interface CoreProveedorFeignClient {

    @GetMapping("/api/tesoreria/core/proveedor/{proveedorId}")
    CoreProveedorResponse getProveedorById(@PathVariable("proveedorId") Integer proveedorId);

    @PostMapping("/api/tesoreria/core/proveedor/page")
    PaginatedResponse<CoreProveedorResponse> getPaginated(@RequestBody PageRequest pageRequest);

    @PostMapping("/api/tesoreria/core/proveedor/search")
    List<CoreProveedorResponse> search(@RequestBody List<String> conditions);

    @GetMapping("/api/tesoreria/core/proveedor/cuit/{cuit}")
    CoreProveedorResponse getByCuit(@PathVariable("cuit") String cuit);

    @PostMapping("/api/tesoreria/core/proveedor/")
    CoreProveedorResponse create(@RequestBody Proveedor proveedor);

    @PutMapping("/api/tesoreria/core/proveedor/{proveedorId}")
    CoreProveedorResponse update(@PathVariable("proveedorId") Integer proveedorId,
                                 @RequestBody Proveedor proveedor);

    @DeleteMapping("/api/tesoreria/core/proveedor/{proveedorId}")
    void delete(@PathVariable("proveedorId") Integer proveedorId);
}
