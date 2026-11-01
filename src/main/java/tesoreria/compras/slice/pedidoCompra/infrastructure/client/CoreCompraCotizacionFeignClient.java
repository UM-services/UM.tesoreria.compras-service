package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraCotizacionClient")
public interface CoreCompraCotizacionFeignClient {

    @GetMapping("/api/tesoreria/core/compraCotizacion/pendientes")
    List<CoreCompraCotizacionPendienteResponse> pendientes(@RequestParam("ejercicioId") Integer ejercicioId,
                                                           @RequestParam("dependenciaIds") List<Integer> dependenciaIds);

    @GetMapping("/api/tesoreria/core/compraCotizacion/proveedores-pendientes")
    List<CoreCompraCotizacionProveedorPendienteResponse> proveedoresPendientes(
            @RequestParam("ejercicioId") Integer ejercicioId);

    @GetMapping("/api/tesoreria/core/compraCotizacion")
    List<CoreCompraCotizacionResponse> findAll();    @PostMapping("/api/tesoreria/core/compraCotizacion/search")
    List<CoreCompraCotizacionResponse> search(@RequestBody CoreCompraCotizacionCriteriaRequest request);

    @GetMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}")
    CoreCompraCotizacionResponse findById(@PathVariable("compraCotizacionId") Long compraCotizacionId);

    @PostMapping("/api/tesoreria/core/compraCotizacion")
    CoreCompraCotizacionResponse create(@RequestBody CoreCompraCotizacionRequest request);

    @PutMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}")
    CoreCompraCotizacionResponse update(@PathVariable("compraCotizacionId") Long compraCotizacionId,
                                        @RequestBody CoreCompraCotizacionRequest request);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/enviar")
    CoreCompraCotizacionResponse enviar(@PathVariable("compraCotizacionId") Long compraCotizacionId);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/anular")
    CoreCompraCotizacionResponse anular(@PathVariable("compraCotizacionId") Long compraCotizacionId,
                                        @RequestBody CoreMotivoRequest request);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/respuesta")
    CoreCompraCotizacionProveedorResponse registrarRespuesta(
            @PathVariable("compraCotizacionId") Long compraCotizacionId,
            @PathVariable("compraCotizacionProveedorId") Long compraCotizacionProveedorId,
            @RequestBody CoreCompraCotizacionRespuestaRequest request);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/no-cotiza")
    CoreCompraCotizacionProveedorResponse marcarNoCotiza(
            @PathVariable("compraCotizacionId") Long compraCotizacionId,
            @PathVariable("compraCotizacionProveedorId") Long compraCotizacionProveedorId,
            @RequestBody CoreMotivoRequest request);

    @GetMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/comparacion")
    CoreCompraCotizacionResponse comparacion(@PathVariable("compraCotizacionId") Long compraCotizacionId);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/recomendar")
    CoreCompraCotizacionResponse recomendar(@PathVariable("compraCotizacionId") Long compraCotizacionId,
                                            @RequestBody CoreRecomendarCompraCotizacionRequest request);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/cerrar")
    CoreCompraCotizacionResponse cerrar(@PathVariable("compraCotizacionId") Long compraCotizacionId);

    @PostMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/adjudicar")
    CoreCompraAdjudicacionResponse adjudicar(@PathVariable("compraCotizacionId") Long compraCotizacionId,
                                             @RequestBody CoreAdjudicarCompraCotizacionRequest request);

    @GetMapping("/api/tesoreria/core/compraCotizacion/{compraCotizacionId}/adjudicacion")
    CoreCompraAdjudicacionResponse adjudicacion(@PathVariable("compraCotizacionId") Long compraCotizacionId);
}
