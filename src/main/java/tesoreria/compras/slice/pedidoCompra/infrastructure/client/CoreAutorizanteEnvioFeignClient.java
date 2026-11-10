package tesoreria.compras.slice.pedidoCompra.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-core-service", contextId = "coreAutorizanteEnvioClient")
public interface CoreAutorizanteEnvioFeignClient {

    @GetMapping("/api/tesoreria/core/compras/autorizanteEnvio/usuario/{usuarioId}")
    CoreAutorizanteEnvioResponse getDependencias(@PathVariable("usuarioId") Long usuarioId);
}
