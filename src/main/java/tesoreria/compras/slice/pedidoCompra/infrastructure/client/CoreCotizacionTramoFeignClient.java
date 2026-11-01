package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCotizacionTramoClient")
public interface CoreCotizacionTramoFeignClient {

    @GetMapping("/api/tesoreria/core/compraCotizacionTramo")
    List<CoreCotizacionTramoResponse> findAll();

    @GetMapping("/api/tesoreria/core/compraCotizacionTramo/minimo")
    CoreCotizacionMinimoResponse minimo(@RequestParam("ejercicioId") Integer ejercicioId,
                                        @RequestParam("monto") BigDecimal monto);

    @PostMapping("/api/tesoreria/core/compraCotizacionTramo")
    CoreCotizacionTramoResponse create(@RequestBody CoreCotizacionTramoRequest request);

    @PutMapping("/api/tesoreria/core/compraCotizacionTramo/{cotizacionTramoId}")
    CoreCotizacionTramoResponse update(@PathVariable("cotizacionTramoId") Long cotizacionTramoId,
                                       @RequestBody CoreCotizacionTramoRequest request);

    @DeleteMapping("/api/tesoreria/core/compraCotizacionTramo/{cotizacionTramoId}")
    void delete(@PathVariable("cotizacionTramoId") Long cotizacionTramoId);
}
