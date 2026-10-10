package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreCompraAutoridadClient")
public interface CoreCompraAutoridadFeignClient {

    @GetMapping("/api/tesoreria/core/compraAutoridadUsuario/limite/{usuarioId}/{ejercicioId}")
    CoreLimiteAutorizacionResponse getLimite(@PathVariable("usuarioId") Integer usuarioId,
                                             @PathVariable("ejercicioId") Integer ejercicioId);
}
