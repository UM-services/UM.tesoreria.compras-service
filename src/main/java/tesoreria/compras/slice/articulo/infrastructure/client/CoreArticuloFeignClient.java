package tesoreria.compras.slice.articulo.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tesoreria.compras.model.PageRequest;
import tesoreria.compras.model.PaginatedResponse;
import tesoreria.compras.slice.articulo.domain.model.Articulo;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreArticuloClient")
public interface CoreArticuloFeignClient {

    @GetMapping("/api/tesoreria/core/articulo/{articuloId}")
    CoreArticuloResponse getArticuloById(@PathVariable("articuloId") Long articuloId);

    @PostMapping("/api/tesoreria/core/articulo/search")
    List<CoreArticuloSearchResponse> searchArticulos(@RequestBody List<String> conditions);

    @PostMapping("/api/tesoreria/core/articulo/tipo/{tipo}/page")
    PaginatedResponse<CoreArticuloResponse> getPaginatedByTipo(
            @PathVariable("tipo") String tipo,
            @RequestBody PageRequest pageRequest);

    @GetMapping("/api/tesoreria/core/articulo/new")
    CoreArticuloResponse getNewArticulo();

    @PostMapping("/api/tesoreria/core/articulo/")
    CoreArticuloResponse createArticulo(@RequestBody Articulo articulo);

    @PutMapping("/api/tesoreria/core/articulo/{articuloId}")
    CoreArticuloResponse updateArticulo(
            @PathVariable("articuloId") Long articuloId,
            @RequestBody Articulo articulo);

    @DeleteMapping("/api/tesoreria/core/articulo/{articuloId}")
    void deleteArticulo(@PathVariable("articuloId") Long articuloId);
}
