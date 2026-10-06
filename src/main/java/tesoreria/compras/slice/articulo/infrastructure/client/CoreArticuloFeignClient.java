package tesoreria.compras.slice.articulo.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreArticuloClient")
public interface CoreArticuloFeignClient {

    @GetMapping("/api/tesoreria/core/articulo/{articuloId}")
    CoreArticuloResponse getArticuloById(@PathVariable("articuloId") Long articuloId);

    @PostMapping("/api/tesoreria/core/articulo/search")
    List<CoreArticuloSearchResponse> searchArticulos(@RequestBody List<String> conditions);
}
