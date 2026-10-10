package tesoreria.compras.slice.ubicacionArticulo.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tesoreria.compras.slice.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreUbicacionArticuloClient")
public interface CoreUbicacionArticuloFeignClient {

    @GetMapping("/api/tesoreria/core/ubicacionArticulo/articulo/{articuloId}")
    List<CoreUbicacionArticuloResponse> getByArticulo(@PathVariable("articuloId") Long articuloId);

    @PostMapping("/api/tesoreria/core/ubicacionArticulo/")
    CoreUbicacionArticuloResponse save(@RequestBody UbicacionArticulo ubicacionArticulo);
}
