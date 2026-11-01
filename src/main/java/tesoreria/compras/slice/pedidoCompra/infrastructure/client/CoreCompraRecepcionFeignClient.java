package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraRecepcionClient")
public interface CoreCompraRecepcionFeignClient {

    @GetMapping("/api/tesoreria/core/compraRecepcion/pendientes")
    List<CoreCompraRecepcionPendienteResponse> pendientes(@RequestParam("ejercicioId") Integer ejercicioId);

    @GetMapping("/api/tesoreria/core/compraRecepcion")
    List<CoreCompraRecepcionResponse> findAll();

    @PostMapping("/api/tesoreria/core/compraRecepcion/search")
    List<CoreCompraRecepcionResponse> search(@RequestBody CoreCompraRecepcionCriteriaRequest request);

    @GetMapping("/api/tesoreria/core/compraRecepcion/{compraRecepcionId}")
    CoreCompraRecepcionResponse findById(@PathVariable("compraRecepcionId") Long compraRecepcionId);

    @GetMapping("/api/tesoreria/core/compraRecepcion/por-formalizacion/{compraFormalizacionId}")
    CoreCompraRecepcionPendienteResponse porFormalizacion(
            @PathVariable("compraFormalizacionId") Long compraFormalizacionId);

    @PostMapping("/api/tesoreria/core/compraRecepcion")
    CoreCompraRecepcionResponse registrar(@RequestBody CoreCompraRecepcionRequest request);

    @PostMapping("/api/tesoreria/core/compraRecepcion/formalizacion/{compraFormalizacionId}/cerrar")
    void cerrar(@PathVariable("compraFormalizacionId") Long compraFormalizacionId,
                @RequestBody CoreCerrarCompraRecepcionRequest request);
}
