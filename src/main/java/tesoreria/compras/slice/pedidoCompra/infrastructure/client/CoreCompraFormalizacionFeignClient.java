package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraFormalizacionClient")
public interface CoreCompraFormalizacionFeignClient {

    @GetMapping("/api/tesoreria/core/compraFormalizacion/pendientes")
    List<CoreCompraFormalizacionPendienteResponse> pendientes();

    @GetMapping("/api/tesoreria/core/compraFormalizacion/pendientes/{ejercicioId}")
    List<CoreCompraFormalizacionPendienteResponse> pendientesPorEjercicio(
            @PathVariable("ejercicioId") Integer ejercicioId);

    @GetMapping("/api/tesoreria/core/compraFormalizacion")
    List<CoreCompraFormalizacionResponse> findAll();

    @PostMapping("/api/tesoreria/core/compraFormalizacion/search")
    List<CoreCompraFormalizacionResponse> search(@RequestBody CoreCompraFormalizacionCriteriaRequest request);

    @GetMapping("/api/tesoreria/core/compraFormalizacion/{compraFormalizacionId}")
    CoreCompraFormalizacionResponse findById(@PathVariable("compraFormalizacionId") Long compraFormalizacionId);

    @GetMapping("/api/tesoreria/core/compraFormalizacion/por-cotizacion/{compraCotizacionId}")
    CoreCompraFormalizacionResponse porCotizacion(@PathVariable("compraCotizacionId") Long compraCotizacionId);

    @PostMapping("/api/tesoreria/core/compraFormalizacion")
    CoreCompraFormalizacionResponse formalizar(@RequestBody CoreCompraFormalizacionRequest request);
}
